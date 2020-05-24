package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.LoginRequest
import br.com.meiadois.decole.data.network.request.RegisterRequest
import br.com.meiadois.decole.data.network.request.UserUpdateRequest
import br.com.meiadois.decole.data.network.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(
    private val client: DecoleClient,
    private val db: AppDatabase
) : RequestHandler() {

    suspend fun login(email: String, password: String): LoginResponse {
        return callClient {
            client.login(LoginRequest(email, password))
        }
    }

    suspend fun register(username: String, email: String, password: String): RegisterResponse {
        return callClient {
            client.register(RegisterRequest(username, email, password))
        }
    }

    suspend fun introduce(): IntroduceResponse {
        db.getUserDao().updateIntroducedStatus(
            db.getUserDao().findJWT()!!,
            true
        )
        return callClient {
            client.introduce()
        }
    }

    suspend fun getUserCompany(): CompanyResponse {
        return callClient {
            client.getUserCompany()
        }
    }

    suspend fun updateUserCompany(
        name: RequestBody,
        cep: RequestBody,
        cnpj: RequestBody,
        description: RequestBody,
        segmentId: RequestBody,
        cellphone: RequestBody,
        email: RequestBody,
        visible: RequestBody,
        city: RequestBody,
        neighborhood: RequestBody,
        thumbnail: MultipartBody.Part,
        banner: MultipartBody.Part
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
        name: RequestBody,
        cep: RequestBody,
        cnpj: RequestBody,
        description: RequestBody,
        segmentId: RequestBody,
        cellphone: RequestBody,
        email: RequestBody,
        visible: RequestBody,
        city: RequestBody,
        neighborhood: RequestBody,
        thumbnail: MultipartBody.Part,
        banner: MultipartBody.Part
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

    suspend fun listUserMatches(): List<LikeResponse> {
        return callClient {
            client.listUserMatches()
        }
    }

    suspend fun updateUser(name: String, email: String): UserUpdateResponse {
        return callClient {
            client.updateUser(UserUpdateRequest(name, email))
        }
    }

    suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().find()

}