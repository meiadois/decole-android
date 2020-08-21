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
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    var email: String = ""
    var emailErrorMessage: String? = null
    var passwordErrorMessage: String? = null
    var password: String = ""
    var authListener: AuthListener? = null

    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()
        val isValid = validateForm(view)
        if (isValid)
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
                    authListener?.onFailure(
                        if (ex.code == 404)
                            view.context.getString(R.string.wrong_login_form_data_error_message)
                        else
                            null
                    )
                } catch (ex: NoInternetException) {
                    authListener?.onFailure(ex.message!!)
                } catch (ex: Exception) {
                    Firebase.crashlytics.recordException(ex)
                    authListener?.onFailure(null)
                }
            }
        authListener?.setErrorMessages(isValid)
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

    private fun validateForm(view: View): Boolean {
        val isValid = validatePassword(view)
        return validateEmail(view) && isValid
    }

    @Suppress("SameParameterValue")
    private fun validatePassword(view: View): Boolean {
        if (password.trim().isEmpty()) {
            passwordErrorMessage = view.context.getString(R.string.empty_password_error_message)
            return false
        }
        passwordErrorMessage = null
        return true
    }

    private fun validateEmail(view: View): Boolean {
        if (email.trim().isEmpty()) {
            emailErrorMessage = view.context.getString(R.string.empty_email_error_message)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailErrorMessage = view.context.getString(R.string.email_invalid_error_message)
            return false
        }
        emailErrorMessage = null
        return true
    }
}