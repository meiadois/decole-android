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
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

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
        val isValid = validateForm(view)
        if (isValid)
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
                    authListener?.onFailure(
                        if (ex.code == 400)
                            ex.message!!.replace("[", "").replace("]", "")
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

    private fun validateForm(view: View): Boolean {
        val minPasswordLength = 6
        val maxPasswordLength = 128

        var isValid = validateEmail(view)
        isValid = validateUsername(view) && isValid
        isValid = validatePassword(view, minPasswordLength, maxPasswordLength) && isValid
        return validateConfirmPassword(view, minPasswordLength, maxPasswordLength) && isValid
    }

    private fun validateUsername(view: View): Boolean {
        if (name.trim().isEmpty()) {
            nameErrorMessage = view.context.getString(R.string.username_empty)
            return false
        }
        nameErrorMessage = null
        return true
    }

    @Suppress("SameParameterValue")
    private fun validatePassword(view: View, min: Int, max: Int): Boolean {
        if (password.trim().isEmpty()) {
            passwordErrorMessage = view.context.getString(R.string.empty_password_error_message)
            return false
        }
        if (password.length < min) {
            passwordErrorMessage = view.context.getString(
                R.string.min_text_length_error_message,
                view.context.getString(R.string.ask_for_new_password), min)
            return false
        }
        if (password.length > max) {
            passwordErrorMessage = view.context.getString(
                R.string.max_text_length_error_message,
                view.context.getString(R.string.ask_for_new_password), max)
            return false
        }
        passwordErrorMessage = null
        return true
    }

    @Suppress("SameParameterValue")
    private fun validateConfirmPassword(view: View, min: Int, max: Int): Boolean {
        if (confirmPassword.trim().isEmpty()) {
            confirmPasswordErrorMessage = view.context.getString(R.string.empty_confirm_password_error_message)
            return false
        }
        if (confirmPassword.length < min) {
            confirmPasswordErrorMessage = view.context.getString(
                R.string.min_text_length_error_message,
                view.context.getString(R.string.ask_for_password_confirmation), min)
            return false
        }
        if (confirmPassword.length > max) {
            confirmPasswordErrorMessage = view.context.getString(
                R.string.max_text_length_error_message,
                view.context.getString(R.string.ask_for_password_confirmation), max)
            return false
        }
        if (password.compareTo(confirmPassword) != 0) {
            confirmPasswordErrorMessage = view.context.getString(R.string.confirm_password_diff_error_message)
            return false
        }
        confirmPasswordErrorMessage = null
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