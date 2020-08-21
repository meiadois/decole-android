package br.com.meiadois.decole.presentation.user.account.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.listener.AccountListener
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.validation.*
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class AccountChangePasswordViewModel(private val userRepository: UserRepository) : ViewModel() {
    var currentPassword: String? = String()
    var newPassword: String? = String()
    var confirmPassword: String? = String()

    var accountListener: AccountListener? = null

    // region onEvent listeners
    fun onTextFieldChange(textInputLayout: TextInputLayout): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            textInputLayout.error = null
        }
    }

    fun onSaveButtonClick(view: View) {
        if (validateModel(view)) {
            Coroutines.main {
                try {
                    accountListener?.onActionStarted()
                    userRepository.changeUserPassword(currentPassword!!, newPassword!!)
                    accountListener?.onActionSuccess()
                } catch (ex: ClientException) {
                    accountListener?.onActionError(
                        if (ex.code == 404)
                            view.context.getString(R.string.change_password_error_message)
                        else
                            null
                    )
                    Log.i(
                        "ChangePassword.Cli", "\nmessage: ${ex.message ?: "no message"}" +
                                "\ncode: ${ex.code}" +
                                "\ncause: ${ex.cause ?: "no cause"}"
                    )
                } catch (ex: Exception) {
                    Firebase.crashlytics.recordException(ex)
                    accountListener?.onActionError(null)
                    Log.i(
                        "ChangePassword.Ex", "\nmessage: ${ex.message ?: "no message"}" +
                                "\nmessage: ${ex.cause ?: "no cause"}"
                    )
                }
            }
        }
    }
    // endregion

    // region validations
    private fun validateModel(view: View): Boolean {
        val minPasswordLength = 6
        val maxPasswordLength = 128
        var isValid = StringValidator(currentPassword)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.changePassword_currentPass_hint)
                    )
                )
            )
            .addValidation(
                MinLengthRule(
                    minPasswordLength,
                    view.context.getString(
                        R.string.min_text_length_error_message,
                        view.context.getString(R.string.changePassword_currentPass_hint),
                        minPasswordLength
                    )
                )
            )
            .addValidation(
                MaxLengthRule(
                    maxPasswordLength,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.changePassword_currentPass_hint),
                        maxPasswordLength
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.USER_CURRENTPASSWORD,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(newPassword)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.changePassword_newPass_hint)
                    )
                )
            )
            .addValidation(
                MinLengthRule(
                    minPasswordLength,
                    view.context.getString(
                        R.string.min_text_length_error_message,
                        view.context.getString(R.string.changePassword_newPass_hint),
                        minPasswordLength
                    )
                )
            )
            .addValidation(
                MaxLengthRule(
                    maxPasswordLength,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.changePassword_newPass_hint),
                        maxPasswordLength
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.USER_NEWPASSWORD,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(confirmPassword)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.changePassword_confirmNewPass_hint)
                    )
                )
            )
            .addValidation(
                MinLengthRule(
                    minPasswordLength,
                    view.context.getString(
                        R.string.min_text_length_error_message,
                        view.context.getString(R.string.changePassword_confirmNewPass_hint),
                        minPasswordLength
                    )
                )
            )
            .addValidation(
                MaxLengthRule(
                    maxPasswordLength,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.changePassword_confirmNewPass_hint),
                        maxPasswordLength
                    )
                )
            )
            .addValidation(
                EqualsTo(
                    newPassword,
                    view.context.getString(
                        R.string.fields_are_not_equals_error_message,
                        view.context.getString(R.string.changePassword_newPass_hint),
                        view.context.getString(R.string.changePassword_confirmNewPass_hint)
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.USER_CONFIRMPASSWORD,
                    it.error
                )
            }
            .validate()

        return isValid
    }
    // endregion
}