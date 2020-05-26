package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.*
import br.com.meiadois.decole.data.network.response.*

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

    suspend fun sendPwResetEmail(email: String): PwResetEmailResponse {
        return callClient {
            client.sendPwResetEmail(PwResetEmailRequest(email))
        }
    }

    suspend fun validatePwResetToken(code: String): ValidatePwResetCodeResponse {
        return callClient {
            client.validatePwResetToken(ValidatePwResetCodeRequest(code))
        }
    }

    suspend fun resetPassword(code: String, password: String): Unit {
        return callClient {
            client.resetPassword(ResetPasswordRequest(code, password))
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
        thumbnail: String = "",
        banner: String = ""
    ): CompanyResponse {
        return callClient {
            client.updateUserCompany(
                CompanyRequest(
                    name,
                    description,
                    segmentId,
                    cnpj,
                    cellphone,
                    email,
                    cep,
                    city,
                    neighborhood,
                    visible,
                    thumbnail,
                    banner
                )
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
        thumbnail: String = "",
        banner: String = ""
    ): CompanyResponse {
        return callClient {
            client.insertUserCompany(
                CompanyRequest(
                    name,
                    description,
                    segmentId,
                    cnpj,
                    cellphone,
                    email,
                    cep,
                    city,
                    neighborhood,
                    visible,
                    thumbnail,
                    banner
                )
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

    suspend fun changeUserPassword(currentPassword: String, newPassword: String): UserUpdateResponse {
        return callClient {
            client.changeUserPassword(UserChangePasswordRequest(currentPassword, newPassword))
        }
    }

    suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().find()

}