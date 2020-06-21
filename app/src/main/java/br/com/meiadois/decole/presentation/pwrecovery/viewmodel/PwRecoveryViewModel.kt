package br.com.meiadois.decole.presentation.pwrecovery.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.R
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

    fun onHomeNextButtonClicked(context: Context?) {
        homeListener?.onStarted()
        validateEmail(context)
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

    fun onCodeNextButtonClicked(context: Context?) = Coroutines.main {
        codeListener?.onStarted()

        try {
            val res = userRepository.validatePwResetToken(code.trim())
            if (res.isValid) {
                codeListener?.onSuccess()
            } else {
                codeListener?.onFailure(context?.getString(R.string.code_invalid_error_message))
            }
        } catch (ex: ClientException) {
            codeListener?.onFailure(ex.message)
        }
    }

    fun onResetFinishButtonClicked(context: Context?) = Coroutines.main {
        resetListener?.onStarted()
        validatePassword(context)
        validateConfirmPassword(context)

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

    private fun validateEmail(context: Context?) {
        emailErrorMessage =
            if (email.trim().isEmpty()) context?.getString(R.string.empty_email_error_message)
            else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim())
                    .matches()
            ) context?.getString(R.string.email_invalid_error_message)
            else null
    }

    private fun validatePassword(context: Context?) {
        passwordErrorMessage =
            if (password.trim().isEmpty()) context?.getString(R.string.empty_password_error_message)
            else null
    }

    private fun validateConfirmPassword(context: Context?) {
        if (passwordConfirmation.trim().isEmpty()) {
            passwordConfirmationErrorMessage =
                context?.getString(R.string.empty_confirm_password_error_message)
            return
        }
        if (password.compareTo(passwordConfirmation) != 0) {
            passwordConfirmationErrorMessage = context?.getString(R.string.confirm_password_diff_error_message)
            return
        }
        passwordConfirmationErrorMessage = null
        return
    }
}