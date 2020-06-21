package br.com.meiadois.decole.presentation.auth.viewmodel

import android.util.Patterns
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.auth.AuthListener
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.parseToUserEntity
import java.net.SocketTimeoutException

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    var name: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var nameErrorMessage: String? = null
    var emailErrorMessage: String? = null
    var passwordErrorMessage: String? = null
    var confirmPasswordErrorMessage: String? = null

    var authListener: AuthListener? = null

    fun onRegisterButtonClick(view: View) {
        authListener?.onStarted()
        validateUsername(view)
        validateEmail(view)
        validatePassword(view)
        validateConfirmPassword(view)

        if (nameErrorMessage != null || emailErrorMessage != null || passwordErrorMessage != null || confirmPasswordErrorMessage != null) {
            authListener?.onFailure(null)
            return
        }

        Coroutines.main {

            try {
                val res = userRepository.register(name, email.trim(), password.trim())
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

    private fun validateUsername(view: View) {
        nameErrorMessage = if (name.trim().isEmpty()) view.context.getString(R.string.username_empty)
        else null
    }

    private fun validatePassword(view: View) {
        passwordErrorMessage = if (password.trim().isEmpty()) view.context.getString(R.string.empty_password_error_message)
        else null
    }

    private fun validateConfirmPassword(view: View) {
        if (confirmPassword.trim().isEmpty()) {
            confirmPasswordErrorMessage = view.context.getString(R.string.empty_confirm_password_error_message)
            return
        }
        if (password.compareTo(confirmPassword) != 0) {
            confirmPasswordErrorMessage = view.context.getString(R.string.confirm_password_diff_error_message)
            return
        }
        confirmPasswordErrorMessage = null
        return
    }

    private fun validateEmail(view: View) {
        emailErrorMessage = if (email.trim().isEmpty()) view.context.getString(R.string.empty_email_error_message)
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) view.context.getString(R.string.email_invalid_error_message)
        else null

    }
}