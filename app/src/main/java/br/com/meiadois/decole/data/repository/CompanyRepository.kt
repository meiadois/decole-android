package br.com.meiadois.decole.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.LikeRequest
import br.com.meiadois.decole.data.network.request.LikeSenderRequest
import br.com.meiadois.decole.data.network.response.*
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.isFetchNeeded
import br.com.meiadois.decole.util.extension.toCompanyModel
import br.com.meiadois.decole.util.extension.toMyCompany
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import java.util.*

class CompanyRepository(
    private val client: DecoleClient,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : RequestHandler() {
    private val companyUser = MutableLiveData<Company>()

    init {
        companyUser.observeForever{
            saveCompany(it)
        }
    }

    suspend fun getCompanyById(companyId: Int): CompanyResponse {
        return callClient {
            client.getCompanyById(companyId)
        }
    }

    suspend fun getCompaniesBySegment(segmentId: Int): List<CompanySearchResponse>{
        return callClient {
            client.getCompaniesBySegment(segmentId)
        }
    }

    suspend fun getAllCompanies(): List<CompanySearchResponse>{
        return callClient{
            client.getAllCompanies()
        }
    }

    suspend fun getUserCompany(): CompanyResponse {
        return callClient {
            client.getUserCompany()
        }
    }

    suspend fun updateUserCompany(
        name: String,
        cep: String,
        cnpj: String,
        description: String,
        segmentId: Int,
        cellphone: String,
        email: String,
        visible: Boolean,
        city: String,
        neighborhood: String,
        thumbnail: RequestBody,
        banner: RequestBody
    ): CompanyResponse {
        return callClient {
            client.updateUserCompany(
                name,
                cep,
                cnpj,
                description,
                segmentId,
                cellphone,
                email,
                visible,
                city,
                neighborhood,
                thumbnail,
                banner
            )
        }
    }

    suspend fun insertUserCompany(
        name: String,
        cep: String,
        cnpj: String,
        description: String,
        segmentId: Int,
        cellphone: String,
        email: String,
        visible: Boolean,
        city: String,
        neighborhood: String,
        thumbnail: RequestBody,
        banner: RequestBody
    ): CompanyResponse {
        return callClient {
            client.insertUserCompany(
                name,
                cep,
                cnpj,
                description,
                segmentId,
                cellphone,
                email,
                visible,
                city,
                neighborhood,
                thumbnail,
                banner
            )
        }
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

    suspend fun sendLikes(senderId: Int, recipientId: Int): LikePutResponse{
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
        val res = callClient {
            client.getUserCompany()
        }
        companyUser.postValue(res.toCompanyModel())
        return res
    }

    private fun saveCompany(company:Company) {
        prefs.saveLastCompanyFetch(System.currentTimeMillis())
        val comp = MyCompany(company.id,company.name,company.thumbnail,company.segment!!.name)
        Coroutines.io {
            db.getCompanyDao().upsert(comp)
        }
    }

    suspend fun getMyCompany(): LiveData<MyCompany> {
        return withContext(Dispatchers.IO) {
            val lastFetch = prefs.getLastCompanyFetch()
            if (lastFetch == 0L || Date(lastFetch).isFetchNeeded())
                return@withContext MutableLiveData(fetchCompany().toMyCompany())
            MutableLiveData(db.getCompanyDao().getUserCompanyLocal())
        }
    }

}