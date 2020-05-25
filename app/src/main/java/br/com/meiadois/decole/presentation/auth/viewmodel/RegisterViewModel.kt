package br.com.meiadois.decole.presentation.auth.viewmodel

import android.util.Patterns
import android.view.View
import androidx.lifecycle.ViewModel
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
        validateUsername()
        validateEmail()
        validatePassword()
        validateConfirmPassword()

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

    private fun validateUsername() {
        nameErrorMessage = if (name.trim().isEmpty()) "Você precisa inserir um nome."
        else null
    }

    private fun validatePassword() {
        passwordErrorMessage = if (password.trim().isEmpty()) "Você precisa inserir uma senha."
        else null
    }

    private fun validateConfirmPassword() {
        if (confirmPassword.trim().isEmpty()) {
            confirmPasswordErrorMessage = "Você precisa inserir a confirmação da senha."
            return
        }
        if (password.compareTo(confirmPassword) != 0) {
            confirmPasswordErrorMessage = "As senhas não coincidem."
            return
        }
        confirmPasswordErrorMessage = null
        return
    }

    private fun validateEmail() {
        emailErrorMessage = if (email.trim().isEmpty()) "Você precisa inserir seu e-mail."
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Você precisa inserir um e-mail válido"
        else null

    }
}