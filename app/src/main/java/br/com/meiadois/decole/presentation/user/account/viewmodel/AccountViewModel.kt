package br.com.meiadois.decole.presentation.user.account.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.network.response.CepResponse
import br.com.meiadois.decole.data.repository.*
import br.com.meiadois.decole.presentation.user.account.AccountListener
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.binding.UserAccountData
import br.com.meiadois.decole.presentation.user.account.binding.UserSocialNetworksData
import br.com.meiadois.decole.presentation.user.account.validation.*
import br.com.meiadois.decole.service.LogOutService
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.extension.toCompanyAccountData
import br.com.meiadois.decole.util.extension.toSegmentModelList
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AccountViewModel(
    private val segmentRepository: SegmentRepository,
    private val companyRepository: CompanyRepository,
    private val userRepository: UserRepository,
    private val cepRepository: CepRepository,
    private val logOutService: LogOutService
) : ViewModel() {
    var companyData: MutableLiveData<CompanyAccountData> = MutableLiveData<CompanyAccountData>()
    var userData: MutableLiveData<UserAccountData> = MutableLiveData<UserAccountData>()
    var userNetworksData: MutableLiveData<UserSocialNetworksData> = MutableLiveData<UserSocialNetworksData>()

    var segments: MutableLiveData<List<Segment>> = MutableLiveData<List<Segment>>()

    var accountListener: AccountListener? = null
    private var isUpdatingCompany: Boolean = false
    private var isUpdatingInstagram: Boolean = false
    private var isUpdatingFacebook: Boolean = false

    // region initializer methods
    init {
        getUserSocialNetworks()
        getUserCompany()
        getSegments()
        getUser()
    }

    private fun getSegments() {
        Coroutines.main {
            try {
                segments.value = segmentRepository.getAllSegments().toSegmentModelList()
            } catch (ex: Exception) {
                Log.i("AccountViewModel.init", ex.message ?: "no error message")
            }
        }
    }

    private fun getUser() {
        try {
            userRepository.getUser().observeForever { user ->
                userData.value =
                    if (user != null) UserAccountData(user.name, user.email) else UserAccountData()
            }
        } catch (ex: Exception) {
            userData.value = UserAccountData()
            Log.i("AccountViewModel.init", ex.message ?: "no error message")
        }
    }

    private fun getUserCompany() {
        Coroutines.main {
            try {
                companyData.value = companyRepository.getUserCompany().toCompanyAccountData()
                companyData.value!!.thumbnail.name = getFileName(companyData.value!!.thumbnail.path)
                companyData.value!!.banner.name = getFileName(companyData.value!!.banner.path)
                isUpdatingCompany = true
            } catch (ex: Exception) {
                companyData.value = CompanyAccountData(email = userData.value!!.email)
                Log.i("AccountViewModel.init", ex.message ?: "no error message")
            }
        }
    }

    private fun getUserSocialNetworks() {
        Coroutines.main {
            try {
                val userSocialNetworksData = UserSocialNetworksData()
                userRepository.getUserAccounts().map {
                    if (it.channel!!.name == INSTAGRAM_CHANNEL){
                        userSocialNetworksData.instagram = it.username
                        isUpdatingInstagram = true
                    }
                    if (it.channel.name == FACEBOOK_CHANNEL){
                        userSocialNetworksData.facebook = it.username
                        isUpdatingFacebook = true
                    }
                }
                userNetworksData.value = userSocialNetworksData
            } catch (ex: Exception) {
                userNetworksData.value = UserSocialNetworksData()
                Log.i("AccountViewModel.init", ex.message ?: "no error message")
            }
        }
    }
    // endregion

    // region On events
    fun onTextFieldChange(textInputLayout: TextInputLayout): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { textInputLayout.error = null }
    }

    fun onCepFieldChange(cep: String) {
        if (ValidCepRule(String()).validate(cep)) {
            Coroutines.main {
                try {
                    val cepResponse: CepResponse = cepRepository.getCep(cep.replace("-", ""))
                    companyData.value?.setNeighborhoodAndNotify(cepResponse.neighborhood)
                    companyData.value?.setCityAndNotify(cepResponse.city)
                } catch (ex: Exception) {
                    Log.i("CepException", ex.message ?: "no error message")
                }
            }
        }
    }

    fun onSearchVisibilityChange(isChecked: Boolean) {
        companyData.value.apply {
            this?.let {
                it.visible = isChecked
            }
        }
    }

    suspend fun onLogOutButtonClick() = logOutService.perform().join()

    fun onSaveButtonClick(view: View) {
        trimProperties()
        if (validateModels(view)) {
            Coroutines.main {
                accountListener?.onActionStarted()
                try {
                    /*
                        TODO: check if in edit mode you will need to send the same image that
                          already is in server or if you can send another value to indicate
                           that the API can keep the same old images
                    */
                    if (isUpdatingCompany) companyRepository.updateUserCompany(
                        companyData.value!!.name,
                        companyData.value!!.cep,
                        companyData.value!!.cnpj,
                        companyData.value!!.description,
                        companyData.value!!.segmentId,
                        companyData.value!!.cellphone,
                        companyData.value!!.email,
                        companyData.value!!.visible,
                        companyData.value!!.city,
                        companyData.value!!.neighborhood,
                        getMultipartBodyPart(
                            companyData.value!!.thumbnail.path,
                            companyData.value!!.thumbnail.type,
                            "thumbnail"),
                        getMultipartBodyPart(
                            companyData.value!!.banner.path,
                            companyData.value!!.banner.type,
                            "banner")
                    ) else companyRepository.insertUserCompany(
                        companyData.value!!.name,
                        companyData.value!!.cep,
                        companyData.value!!.cnpj,
                        companyData.value!!.description,
                        companyData.value!!.segmentId,
                        companyData.value!!.cellphone,
                        companyData.value!!.email,
                        companyData.value!!.visible,
                        companyData.value!!.city,
                        companyData.value!!.neighborhood,
                        getMultipartBodyPart(
                            companyData.value!!.thumbnail.path,
                            companyData.value!!.thumbnail.type,
                            "thumbnail"),
                        getMultipartBodyPart(
                            companyData.value!!.banner.path,
                            companyData.value!!.banner.type,
                            "banner")
                    )

                    /* TODO: discomment this when finish the create/edit of company
                    userRepository.updateUser(userData.value!!.name, userData.value!!.email)

                    if (isUpdatingInstagram) {
                        if (userNetworksData.value!!.instagram.isEmpty())
                            userRepository.deleteUserAccount(INSTAGRAM_CHANNEL)
                        else
                            userRepository.updateUserAccount(INSTAGRAM_CHANNEL, userNetworksData.value!!.instagram)
                    } else if (userNetworksData.value!!.instagram.isNotEmpty())
                        userRepository.insertUserAccount(INSTAGRAM_CHANNEL, userNetworksData.value!!.instagram)

                    if (isUpdatingFacebook) {
                        if (userNetworksData.value!!.facebook.isEmpty())
                            userRepository.deleteUserAccount(FACEBOOK_CHANNEL)
                        else
                            userRepository.updateUserAccount(FACEBOOK_CHANNEL, userNetworksData.value!!.facebook)
                    } else if (userNetworksData.value!!.facebook.isNotEmpty())
                        userRepository.insertUserAccount(FACEBOOK_CHANNEL, userNetworksData.value!!.facebook)*/

                    accountListener?.onActionSuccess()
                } catch (ex: ClientException) {
                    accountListener?.onActionError(
                        if (ex.code == 400)
                            Regex(" \\[(.*?)\\]").replace(ex.message!!, "")
                        else
                            null
                    )
                    Log.i("AccountFormEx.Cli", "" +
                            "\nstatus code: ${ex.code}" +
                            "\nmessage: ${ex.message ?: "no error message"}" +
                            "\ncause: ${ex.cause?.toString() ?: "no cause"}")
                } catch (ex: Exception) {
                    accountListener?.onActionError(null)
                    Log.i("AccountFormEx.Ex", "" +
                            "\nmessage: ${ex.message ?: "no error message"}" +
                            "\ncause: ${ex.cause?.toString() ?: "no cause"}")
                }
            }
        }
    }

    private fun getMultipartBodyPart(imagePath: String, imageType: String, parameterName: String): MultipartBody.Part {
        val file = File(imagePath)
        return MultipartBody.Part.createFormData(
            parameterName,
            file.name,
            RequestBody.create(
                MediaType.parse(imageType),
                file
            )
        )
    }

    private fun trimProperties(){
        if (userData.value != null){
            userData.value!!.name = userData.value!!.name.trim()
            userData.value!!.email = userData.value!!.email.trim()
        }
        if (companyData.value != null){
            companyData.value!!.name = companyData.value!!.name.trim()
            companyData.value!!.city = companyData.value!!.city.trim()
            companyData.value!!.email = companyData.value!!.email.trim()
            companyData.value!!.description = companyData.value!!.description.trim()
            companyData.value!!.neighborhood = companyData.value!!.neighborhood.trim()
        }
        if (userNetworksData.value != null){
            userNetworksData.value!!.instagram = userNetworksData.value!!.instagram.trim()
            userNetworksData.value!!.facebook = userNetworksData.value!!.facebook.trim()
        }
    }
    // endregion

    // region Validation
    private fun validateModels(view: View): Boolean {
        var isValid = validateUserModel(view)
        isValid = validateCompanyModel(view) and isValid
        return validateUserSocialNetworks(view) and isValid
    }

    private fun validateUserModel(view: View): Boolean {
        val user: UserAccountData = userData.value!!

        var isValid = StringValidator(user.name)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_me_name_hint))))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.USER_NAME, it.error) }
            .validate()

        isValid = isValid and StringValidator(user.email)
            .addValidation(ValidEmailRule(view.context.getString(R.string.invalid_email_error_message)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.USER_EMAIL, it.error) }
            .validate()

        return isValid
    }

    private fun validateCompanyModel(view: View): Boolean {
        val company: CompanyAccountData = companyData.value!!

        var isValid = StringValidator(company.name)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_name_hint))))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_NAME, it.error) }
            .validate()

        isValid = isValid and IntegerValidator(company.segmentId)
            .addValidation(MinRule(1,
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_segment_hint))))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_SEGMENT, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.description)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_description_hint))))
            .addValidation(MaxLengthRule(144,
                view.context.getString(
                    R.string.max_text_length_error_message,
                    view.context.getString(R.string.account_company_description_hint), 144)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_DESCRIPTION, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.cep)
            .addValidation(ValidCepRule(view.context.getString(R.string.invalid_cep_error_message)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_CEP, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.city)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_city_hint))))
            .addValidation(MaxLengthRule(28,
                view.context.getString(
                    R.string.max_text_length_error_message,
                    view.context.getString(R.string.account_company_city_hint), 28)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_CITY, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.neighborhood)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_neighborhood_hint))))
            .addValidation(MaxLengthRule(35,
                view.context.getString(
                    R.string.max_text_length_error_message,
                    view.context.getString(R.string.account_company_neighborhood_hint), 35)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_NEIGHBORHOOD, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.cnpj)
            .addValidation(ValidCnpjRule(view.context.getString(R.string.invalid_cnpj_error_message)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_CNPJ, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.cellphone)
            .addValidation(ValidTelephoneRule(view.context.getString(R.string.invalid_telephone_error_message)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_CELLPHONE, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.email)
            .addValidation(ValidEmailRule(view.context.getString(R.string.invalid_email_error_message)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_EMAIL, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.thumbnail.path)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_logo_hint))))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_THUMBNAIL, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.banner.path)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_banner))))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.COMPANY_BANNER, it.error) }
            .validate()

        return isValid
    }

    private fun validateUserSocialNetworks(view: View): Boolean {
        val socialNetworks: UserSocialNetworksData = userNetworksData.value!!

        var isValid = StringValidator(socialNetworks.instagram)
            .addValidation(MaxLengthRule(30,
                view.context.getString(
                    R.string.max_text_length_error_message,
                    view.context.getString(R.string.socialNetwork_instagram_hint), 30)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.USER_INSTAGRAM, it.error) }
            .validate()

        isValid = isValid and StringValidator(socialNetworks.facebook)
            .addValidation(MaxLengthRule(50,
                view.context.getString(
                    R.string.max_text_length_error_message,
                    view.context.getString(R.string.socialNetwork_facebook_hint), 50)))
            .addErrorCallback { accountListener?.riseValidationError(FieldsEnum.USER_FACEBOOK, it.error) }
            .validate()

        return isValid
    }
    // endregion

    fun getFileName(filePath: String): String = Regex(FILE_NAME_IN_DIRECTORY_REGEX_PATTERN)
        .find(filePath)?.value.toString()

    companion object{
        private const val INSTAGRAM_CHANNEL = "Instagram"
        private const val FACEBOOK_CHANNEL = "Facebook"

        private const val FILE_NAME_IN_DIRECTORY_REGEX_PATTERN = "[^/]*$"
    }
}