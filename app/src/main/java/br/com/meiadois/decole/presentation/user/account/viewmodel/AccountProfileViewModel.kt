package br.com.meiadois.decole.presentation.user.account.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.listener.AccountListener
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.binding.UserData
import br.com.meiadois.decole.presentation.user.account.validation.NotNullOrEmptyRule
import br.com.meiadois.decole.presentation.user.account.validation.StringValidator
import br.com.meiadois.decole.presentation.user.account.validation.ValidEmailRule
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.extension.parseToUserAccountData
import br.com.meiadois.decole.util.extension.parseToUserEntity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class AccountProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    var userData: MutableLiveData<UserData> = MutableLiveData()
    var accountListener: AccountListener? = null

    // region initializer methods
    fun init() {
        getUser()
    }

    private fun getUser() {
        try {
            userRepository.getUser().observeForever { user ->
                userData.value = user?.parseToUserAccountData() ?: UserData()
            }
        } catch (ex: Exception) {
            userData.value = UserData()
            throw ex
        }
    }
    // endregion

    // region On events
    fun onTextFieldChange(textInputLayout: TextInputLayout): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            textInputLayout.error = null
        }
    }

    fun onSaveButtonClick(view: View) {
        trimProperties()
        if(validateUserModel(view)) {
            Coroutines.main {
                accountListener?.onActionStarted()
                try {
                    userRepository.updateUser(userData.value!!.name, userData.value!!.email)
                    userRepository.saveUser(userData.value!!.parseToUserEntity())

                    accountListener?.onActionSuccess()
                } catch (ex: ClientException) {
                    accountListener?.onActionError(
                        if (ex.code == 400)
                            Regex(" \\[(.*?)]").replace(ex.message!!, "")
                        else
                            null
                    )
                    Log.i(
                        "ProfileForm.Cli", "" +
                                "\nstatus code: ${ex.code}" +
                                "\nmessage: ${ex.message ?: "no error message"}" +
                                "\ncause: ${ex.cause?.toString() ?: "no cause"}"
                    )
                } catch (ex: Exception) {
                    Firebase.crashlytics.recordException(ex)
                    accountListener?.onActionError(null)
                    Log.i(
                        "ProfileForm.Ex", "" +
                                "\nmessage: ${ex.message ?: "no error message"}" +
                                "\ncause: ${ex.cause?.toString() ?: "no cause"}"
                    )
                }
            }
        }
    }

    private fun trimProperties() {
        if (userData.value != null) {
            userData.value!!.name = userData.value!!.name.trim()
            userData.value!!.email = userData.value!!.email.trim()
        }
    }
    // endregion

    // region Validation
    private fun validateUserModel(view: View): Boolean {
        val user: UserData = userData.value!!

        var isValid = StringValidator(user.name)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.profile_name_hint)
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.USER_NAME,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(user.email)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.profile_mail_hint)
                    )
                )
            )
            .addValidation(
                ValidEmailRule(
                    view.context.getString(R.string.invalid_email_error_message)
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.USER_EMAIL,
                    it.error
                )
            }
            .validate()

        return isValid
    }
    // endregion
}