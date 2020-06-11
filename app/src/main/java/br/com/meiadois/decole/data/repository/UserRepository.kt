package br.com.meiadois.decole.data.repository

import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.localdb.entity.Segment
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.*
import br.com.meiadois.decole.data.network.response.*
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModel


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

    suspend fun resetPassword(code: String, password: String) {
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

    suspend fun updateUser(name: String, email: String): UserUpdateResponse {
        return callClient {
            client.updateUser(UserUpdateRequest(name, email))
        }
    }

    suspend fun changeUserPassword(currentPassword: String, newPassword: String) {
        callClient {
            client.changeUserPassword(UserChangePasswordRequest(currentPassword, newPassword))
        }
    }

    suspend fun getUserAccounts(): List<AccountResponse> {
        return callClient {
            client.getUserAccounts()
        }
    }

    suspend fun insertUserAccount(channelName: String, userName: String): AccountResponse {
        return callClient {
            client.insertUserAccount(AccountRequest(userName, channelName))
        }
    }

    suspend fun updateUserAccount(channelName: String, userName: String): AccountResponse {
        return callClient {
            client.updateUserAccount(channelName, AccountRequest(userName))
        }
    }

    suspend fun deleteUserAccount(channelName: String) {
        client.deleteUserAccount(channelName)
    }

    suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().find()

}