package br.com.meiadois.decole.presentation.auth.viewmodel

import android.content.Intent
import android.util.Patterns
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.auth.AuthListener
import br.com.meiadois.decole.presentation.auth.RegisterActivity
import br.com.meiadois.decole.presentation.pwrecovery.PwRecoveryHomeActivity
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.parseToUserEntity
import java.net.SocketTimeoutException

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    var email: String = ""
    var emailErrorMessage: String? = null
    var passwordErrorMessage: String? = null
    var password: String = ""
    var authListener: AuthListener? = null

    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()
        validateEmail(view)
        validatePassword(view)
        if (emailErrorMessage != null || passwordErrorMessage != null) {
            authListener?.onFailure(null)
            return
        }

        Coroutines.main {

            try {
                val res = userRepository.login(email.trim(), password.trim())

                res.user?.let {
                    userRepository.saveUser(it.parseToUserEntity())
                    authListener?.onSuccess(it.parseToUserEntity(), res.message)
                    return@main
                }
                authListener?.onFailure(res.message!!)
            } catch (ex: ClientException) {
                authListener?.onFailure(ex.message!!)
            } catch (ex: NoInternetException) {
                authListener?.onFailure(ex.message!!)
            } catch (ex: SocketTimeoutException) {
                authListener?.onFailure(ex.message!!)
            }

        }
    }

    fun onRegisterButtonClick(view: View) {
        Intent(view.context, RegisterActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun onRecoveryButtonClick(view: View) {
        Intent(view.context, PwRecoveryHomeActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    private fun validatePassword(view: View) {
        passwordErrorMessage = if (password.trim().isEmpty()) view.context.getString(R.string.empty_password_error_message)
        else null
    }

    private fun validateEmail(view: View) {
        emailErrorMessage = if (email.trim().isEmpty()) view.context.getString(R.string.empty_email_error_message)
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) view.context.getString(R.string.email_invalid_error_message)
        else null

    }
}