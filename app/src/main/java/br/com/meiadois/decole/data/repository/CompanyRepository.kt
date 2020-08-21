package br.com.meiadois.decole.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.webkit.URLUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.DecoleApplication
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.LikeRequest
import br.com.meiadois.decole.data.network.request.LikeSenderRequest
import br.com.meiadois.decole.data.network.response.*
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.presentation.user.account.binding.CompanyData
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.util.*

class CompanyRepository(
    private val client: DecoleClient,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : RequestHandler() {
    private val companyUser = MutableLiveData<Company>()

    init {
        companyUser.observeForever {
            saveCompany(it)
        }
    }

    suspend fun getCompaniesBySegment(segmentId: Int): List<CompanySearchResponse> {
        return callClient {
            client.getCompaniesBySegment(segmentId)
        }
    }

    suspend fun getAllCompanies(): List<CompanySearchResponse> {
        return callClient {
            client.getAllCompanies()
        }
    }

    suspend fun updateUserCompany(
        company: CompanyData,
        thumbnail: MultipartBody.Part?,
        banner: MultipartBody.Part?
    ): CompanyResponse {
        val response = callClient {
            company.let {
                client.updateUserCompany(
                    it.name,
                    it.cep,
                    it.cnpj,
                    it.description,
                    it.segmentId,
                    it.cellphone,
                    it.email,
                    it.visible,
                    it.city,
                    it.neighborhood,
                    thumbnail,
                    banner
                )

            }
        }
        companyUser.postValue(response.toCompanyModel())
        return response
    }

    suspend fun insertUserCompany(
        company: CompanyData,
        thumbnail: MultipartBody.Part,
        banner: MultipartBody.Part
    ): CompanyResponse {
        val response = callClient {
            company.let {
                client.insertUserCompany(
                    it.name,
                    it.cep,
                    it.cnpj,
                    it.description,
                    it.segmentId,
                    it.cellphone,
                    it.email,
                    it.visible,
                    it.city,
                    it.neighborhood,
                    thumbnail,
                    banner
                )
            }
        }
        companyUser.postValue(response.toCompanyModel())
        return response
    }

    suspend fun deletePartnership(likeId: Int, senderId: Int, recipientId: Int): LikePutResponse {
        return callClient {
            client.updateLike(likeId, LikeRequest("deleted", senderId, recipientId))
        }
    }

    suspend fun confirmPartnership(likeId: Int, senderId: Int, recipientId: Int): LikePutResponse {
        return callClient {
            client.updateLike(likeId, LikeRequest("accepted", senderId, recipientId))
        }
    }

    suspend fun cancelPartnership(likeId: Int, senderId: Int, recipientId: Int): LikePutResponse {
        return callClient {
            client.updateLike(likeId, LikeRequest("denied", senderId, recipientId))
        }
    }

    suspend fun deleteLike(likeId: Int) {
        client.deleteLike(likeId)
    }

    suspend fun sendLikes(senderId: Int, recipientId: Int): LikePutResponse {
        return callClient {
            client.sendLike(LikeSenderRequest(senderId, recipientId))
        }
    }

    suspend fun getUserMatches(): List<LikeResponse> {
        return callClient {
            client.getUserMatches()
        }
    }

    suspend fun getUserSentLikes(): List<LikeSentResponse> {
        return callClient {
            client.getUserSentLikes()
        }
    }

    suspend fun getUserReceivedLikes(): List<LikeReceivedResponse> {
        return callClient {
            client.getUserReceivedLikes()
        }
    }

    private suspend fun fetchCompany(): CompanyResponse {
        val response = callClient { client.getUserCompany() }

        companyUser.postValue(response.toCompanyModel())

        val context = DecoleApplication.applicationContext()!!
        saveImageToInternalStorage(context, response.banner, BANNER_IMAGE_NAME)
        saveImageToInternalStorage(context, response.thumbnail, THUMBNAIL_IMAGE_NAME)

        return response
    }

    private fun saveCompany(company: Company) {
        prefs.saveLastCompanyFetch(System.currentTimeMillis())
        Coroutines.io {
            company.segment?.let{
                if (it.id != null)
                    db.getSegmentDao().upsert(it.toSegmentEntity())
            }
            db.getCompanyDao().upsert(company.toCompanyEntity())
        }
    }

    suspend fun getMyCompany(): LiveData<MyCompany> {
        return withContext(Dispatchers.IO) {
            val lastFetch = prefs.getLastCompanyFetch()
            if (lastFetch == 0L || Date(lastFetch).isFetchNeeded())
                return@withContext MutableLiveData(fetchCompany().toMyCompany())
            MutableLiveData(db.getCompanyDao().getUserCompanyWithSegment())
        }
    }

    fun saveImageToInternalStorage(context: Context, path: String, fileName: String, repeat: Boolean = true) {
        if (path.isBlank() || fileName.isBlank()) return

        WeakReference(context).get()?.let {ctx ->
            try {
                val dir = File(ctx.filesDir, IMAGES_FOLDER)

                if (!dir.exists() && !dir.mkdir()) return

                var file = File(dir, fileName)

                if (file.exists() && file.delete())
                    file = File(dir, fileName)

                FileOutputStream(file).apply {
                    val bitmap = if (URLUtil.isValidUrl(path))
                        Glide.with(context).asBitmap().load(path).submit().get()
                    else
                        BitmapFactory.decodeFile(File(path).absolutePath)

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
                    flush()
                    close()
                }
            } catch (ex: Exception) {
                if (repeat) saveImageToInternalStorage(context, path, fileName, false)
                Log.i(
                    "CompanyRepository.Ex", "" +
                            "\nmessage: ${ex.message ?: "no error message"}" +
                            "\ncause: ${ex.cause?.toString() ?: "no cause"}"
                )
            }
        }
    }

    companion object {
        const val THUMBNAIL_IMAGE_NAME = "thumbnail.jpg"
        const val BANNER_IMAGE_NAME = "banner.jpg"
        const val IMAGES_FOLDER = "Images"
    }
}