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
import br.com.meiadois.decole.presentation.user.account.binding.*
import br.com.meiadois.decole.presentation.user.account.validation.*
import br.com.meiadois.decole.service.LogOutService
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
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

    var segments: MutableLiveData<List<Segment>> = MutableLiveData<List<Segment>>()

    var accountListener: AccountListener? = null
    private var isUpdatingCompany: Boolean = false

    // region initializer methods
    suspend fun init() {
        getUser()
        getSegments()
        getUserCompany()
    }

    private suspend fun getSegments() {
        segmentRepository.getAllSegments().observeForever {
            it?.let {
                segments.value = it.parseToSegmentModelList()
            }
        }
    }

    private fun getUser() {
        try {
            userRepository.getUser().observeForever { user ->
                userData.value = user?.parseToUserAccountData() ?: UserAccountData()
            }
        } catch (ex: Exception) {
            userData.value = UserAccountData()
            throw ex
        }
    }

    private suspend fun getUserCompany() {
        try {
            companyRepository.getMyCompany().observeForever {
                if (it != null) {
                    companyData.value = it.toCompanyAccountData()
                    companyData.value!!.thumbnail.name =
                        getFileName(companyData.value!!.thumbnail.path)
                    companyData.value!!.banner.name = getFileName(companyData.value!!.banner.path)
                    isUpdatingCompany = true
                } else
                    companyData.value = CompanyAccountData(email = userData.value!!.email)
            }
        } catch (ex: Exception) {
            companyData.value = CompanyAccountData(email = userData.value!!.email)
            if (ex !is ClientException) throw ex
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

    fun onCepFieldChange(cep: String) {
        if (ValidCepRule(String()).validate(cep)) {
            Coroutines.main {
                try {
                    val cepResponse: CepResponse = cepRepository.getCep(cep.replace("-", ""))
                    companyData.value?.setNeighborhoodAndNotify(cepResponse.neighborhood)
                    companyData.value?.setCityAndNotify(cepResponse.city)
                } catch (ex: Exception) {
                    Log.i("CepException", "\nmessage: ${ex.message ?: "no error message"}\n" +
                            "cause: ${ex.cause ?: "no cause"}\n" +
                            "class: ${ex.javaClass.name}")
                    if (ex !is ClientException)
                        Firebase.crashlytics.recordException(ex)
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
                    val company = companyData.value!!
                    if (isUpdatingCompany) companyRepository.updateUserCompany(
                        company.name,
                        company.cep,
                        company.cnpj,
                        company.description,
                        company.segmentId,
                        company.cellphone,
                        company.email,
                        company.visible,
                        company.city,
                        company.neighborhood,
                        if (company.thumbnail.updated)
                            getMultipartBodyPart(
                                company.thumbnail.path,
                                company.thumbnail.type,
                                "thumbnail"
                            )
                        else null,
                        if (company.banner.updated)
                            getMultipartBodyPart(
                                company.banner.path,
                                company.banner.type,
                                "banner"
                            )
                        else null
                    ) else companyRepository.insertUserCompany(
                        company.name,
                        company.cep,
                        company.cnpj,
                        company.description,
                        company.segmentId,
                        company.cellphone,
                        company.email,
                        company.visible,
                        company.city,
                        company.neighborhood,
                        getMultipartBodyPart(
                            company.thumbnail.path,
                            company.thumbnail.type,
                            "thumbnail"
                        ),
                        getMultipartBodyPart(
                            company.banner.path,
                            company.banner.type,
                            "banner"
                        )
                    )

                    userRepository.updateUser(userData.value!!.name, userData.value!!.email)
                    userRepository.saveUser(userData.value!!.parseToUserEntity())

                    accountListener?.onActionSuccess()
                } catch (ex: NoInternetException) {
                    accountListener?.onActionError(
                        view.context.getString(R.string.no_internet_connection_error_message)
                    )
                } catch (ex: ClientException) {
                    accountListener?.onActionError(
                        if (ex.code == 400)
                            Regex(" \\[(.*?)]").replace(ex.message!!, "")
                        else
                            null
                    )
                    Log.i(
                        "AccountFormEx.Cli", "" +
                                "\nstatus code: ${ex.code}" +
                                "\nmessage: ${ex.message ?: "no error message"}" +
                                "\ncause: ${ex.cause?.toString() ?: "no cause"}"
                    )
                } catch (ex: Exception) {
                    Firebase.crashlytics.recordException(ex)
                    accountListener?.onActionError(null)
                    Log.i(
                        "AccountFormEx.Ex", "" +
                                "\nmessage: ${ex.message ?: "no error message"}" +
                                "\ncause: ${ex.cause?.toString() ?: "no cause"}"
                    )
                }
            }
        }
    }

    private fun getMultipartBodyPart(
        imagePath: String,
        imageType: String,
        parameterName: String
    ): MultipartBody.Part {
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

    private fun trimProperties() {
        if (userData.value != null) {
            userData.value!!.name = userData.value!!.name.trim()
            userData.value!!.email = userData.value!!.email.trim()
        }
        if (companyData.value != null) {
            companyData.value!!.name = companyData.value!!.name.trim()
            companyData.value!!.city = companyData.value!!.city.trim()
            companyData.value!!.email = companyData.value!!.email.trim()
            companyData.value!!.description = companyData.value!!.description.trim()
            companyData.value!!.neighborhood = companyData.value!!.neighborhood.trim()
        }
    }
    // endregion

    // region Validation
    private fun validateModels(view: View): Boolean {
        val isValid = validateUserModel(view)
        return validateCompanyModel(view) and isValid
    }

    private fun validateUserModel(view: View): Boolean {
        val user: UserAccountData = userData.value!!

        var isValid = StringValidator(user.name)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_me_name_hint)
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
            .addValidation(ValidEmailRule(view.context.getString(R.string.invalid_email_error_message)))
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.USER_EMAIL,
                    it.error
                )
            }
            .validate()

        return isValid
    }

    private fun validateCompanyModel(view: View): Boolean {
        val company: CompanyAccountData = companyData.value!!
        val maxLengthNeighborhood = 35
        val maxLengthCity = 28

        var isValid = StringValidator(company.name)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_company_name_hint)
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_NAME,
                    it.error
                )
            }
            .validate()

        isValid = isValid and IntegerValidator(company.segmentId)
            .addValidation(
                MinRule(
                    1,
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_company_segment_hint)
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_SEGMENT,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.description)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_company_description_hint)
                    )
                )
            )
            .addValidation(
                MaxLengthRule(
                    MAX_DESCRIPTION_SIZE,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.account_company_description_hint), MAX_DESCRIPTION_SIZE
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_DESCRIPTION,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.cep)
            .addValidation(ValidCepRule(view.context.getString(R.string.invalid_cep_error_message)))
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_CEP,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.city)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_company_city_hint)
                    )
                )
            )
            .addValidation(
                MaxLengthRule(
                    maxLengthCity,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.account_company_city_hint),
                        maxLengthCity
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_CITY,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.neighborhood)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_company_neighborhood_hint)
                    )
                )
            )
            .addValidation(
                MaxLengthRule(
                    maxLengthNeighborhood,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.account_company_neighborhood_hint),
                        maxLengthNeighborhood
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_NEIGHBORHOOD,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.cnpj)
            .addValidation(ValidCnpjRule(view.context.getString(R.string.invalid_cnpj_error_message)))
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_CNPJ,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.cellphone)
            .addValidation(ValidTelephoneRule(view.context.getString(R.string.invalid_telephone_error_message)))
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_CELLPHONE,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.email)
            .addValidation(ValidEmailRule(view.context.getString(R.string.invalid_email_error_message)))
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_EMAIL,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.thumbnail.path)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_company_logo_hint)
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_THUMBNAIL,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.banner.path)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.account_company_banner)
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_BANNER,
                    it.error
                )
            }
            .validate()

        return isValid
    }
    // endregion

    fun getFileName(filePath: String): String = Regex(FILE_NAME_IN_DIRECTORY_REGEX_PATTERN)
        .find(filePath)?.value.toString()

    companion object {
        private const val FILE_NAME_IN_DIRECTORY_REGEX_PATTERN = "[^/]*$"

        const val MAX_DESCRIPTION_SIZE = 144
    }
}