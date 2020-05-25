package br.com.meiadois.decole.presentation.pwrecovery.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.pwrecovery.listener.CodeListener
import br.com.meiadois.decole.presentation.pwrecovery.listener.HomeListener
import br.com.meiadois.decole.presentation.pwrecovery.listener.ResetListener
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException

class PwRecoveryViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var email: String = ""
    var emailErrorMessage: String? = null
    var code: String = ""
    var codeErrorMessage: String? = null
    var password: String = ""
    var passwordErrorMessage: String? = null
    var passwordConfirmation: String = ""
    var passwordConfirmationErrorMessage: String? = null

    var homeListener: HomeListener? = null
    var codeListener: CodeListener? = null
    var resetListener: ResetListener? = null

    fun onHomeNextButtonClicked() {
        homeListener?.onStarted()
        validateEmail()
        if (emailErrorMessage != null) {
            homeListener?.onFailure(null)
            return
        }

        Coroutines.io {
            try {
                userRepository.sendPwResetEmail(email.trim())
            } catch (ex: ClientException) {
                //ignore
            }
        }

        homeListener?.onSuccess()
    }

    fun onCodeNextButtonClicked() = Coroutines.main {
        codeListener?.onStarted()

        try {
            val res = userRepository.validatePwResetToken(code.trim())
            if (res.isValid) {
                codeListener?.onSuccess()
            } else {
                codeListener?.onFailure("O código não é válido")
            }
        } catch (ex: ClientException) {
            codeListener?.onFailure(ex.message)
        }
    }

    fun onResetFinishButtonClicked() = Coroutines.main {
        resetListener?.onStarted()
        validatePassword()
        validateConfirmPassword()

        if (passwordErrorMessage != null || passwordConfirmationErrorMessage != null) {
            resetListener?.onFailure(null)
            return@main
        }

        try {
            userRepository.resetPassword(code.trim(), password.trim())
        } catch (ex: ClientException) {
            resetListener?.onFailure(ex.message)
        }

        resetListener?.onSuccess()
    }

    private fun validateEmail() {
        emailErrorMessage = if (email.trim().isEmpty()) "Você precisa inserir seu e-mail."
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Você precisa inserir um e-mail válido"
        else null
    }

    private fun validatePassword() {
        passwordErrorMessage = if (password.trim().isEmpty()) "Você precisa inserir uma senha."
        else null
    }

    private fun validateConfirmPassword() {
        if (passwordConfirmation.trim().isEmpty()) {
            passwordConfirmationErrorMessage = "Você precisa inserir a confirmação da senha."
            return
        }
        if (password.compareTo(passwordConfirmation) != 0) {
            passwordConfirmationErrorMessage = "As senhas não coincidem."
            return
        }
        passwordConfirmationErrorMessage = null
        return
    }
}