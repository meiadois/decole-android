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
import br.com.meiadois.decole.data.repository.CepRepository
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.binding.CompanyData
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.binding.UserData
import br.com.meiadois.decole.presentation.user.account.listener.AccountListener
import br.com.meiadois.decole.presentation.user.account.validation.*
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.parseToSegmentModelList
import br.com.meiadois.decole.util.extension.parseToUserAccountData
import br.com.meiadois.decole.util.extension.toCompanyAccountData
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AccountCompanyViewModel(
    private val segmentRepository: SegmentRepository,
    private val companyRepository: CompanyRepository,
    private val userRepository: UserRepository,
    private val cepRepository: CepRepository
) : ViewModel() {
    var companyData: MutableLiveData<CompanyData> = MutableLiveData()
    var segments: MutableLiveData<List<Segment>> = MutableLiveData()
    var userData: MutableLiveData<UserData> = MutableLiveData()

    var accountListener: AccountListener? = null
    private var isUpdatingCompany: Boolean = false

    // region Initializer Methods
    suspend fun init() {
        getUser()
        getSegments()
        getUserCompany()
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

    private suspend fun getSegments() {
        segmentRepository.getAllSegments().observeForever {
            it?.let {
                segments.value = it.parseToSegmentModelList()
            }
        }
    }

    private suspend fun getUserCompany() {
        try {
            companyRepository.getMyCompany().observeForever {
                if (it != null) {
                    companyData.value = it.toCompanyAccountData()
                    isUpdatingCompany = true
                } else
                    companyData.value = CompanyData(email = userData.value!!.email)
            }
        } catch (ex: Exception) {
            companyData.value = CompanyData(email = userData.value!!.email)
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
                    Log.i(
                        "CepException", "\nmessage: ${ex.message ?: "no error message"}\n" +
                                "cause: ${ex.cause ?: "no cause"}\n" +
                                "class: ${ex.javaClass.name}"
                    )
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

    fun onSaveButtonClick(view: View) {
        trimProperties()
        if (validateCompanyModel(view)) {
            Coroutines.main {
                accountListener?.onActionStarted()
                try {
                    companyData.value!!.let {

                        val thumbnailPart: MultipartBody.Part =
                            getMultipartBodyPart(it.thumbnail.path, it.thumbnail.type, "thumbnail")

                        val bannerPart: MultipartBody.Part =
                            getMultipartBodyPart(it.banner.path, it.banner.type, "banner")

                        if (isUpdatingCompany) companyRepository.updateUserCompany(
                            it,
                            if (it.thumbnail.updated) thumbnailPart else null,
                            if (it.banner.updated) bannerPart else null
                        ) else companyRepository.insertUserCompany(
                            it,
                            thumbnailPart,
                            bannerPart
                        )

                        if (it.thumbnail.updated)
                            companyRepository.saveImageToInternalStorage(
                                view.context,
                                it.thumbnail.path,
                                CompanyRepository.THUMBNAIL_IMAGE_NAME
                            )

                        if (it.banner.updated)
                            companyRepository.saveImageToInternalStorage(
                                view.context,
                                it.banner.path,
                                CompanyRepository.BANNER_IMAGE_NAME
                            )
                    }
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
    // endregion

    // region Util
    private fun getMultipartBodyPart(path: String, type: String, name: String): MultipartBody.Part {
        val file = File(path)
        return MultipartBody.Part.createFormData(
            name,
            file.name,
            RequestBody.create(
                MediaType.parse(type),
                file
            )
        )
    }
    // endregion

    // region Validation
    private fun trimProperties() {
        if (companyData.value != null) {
            companyData.value!!.name = companyData.value!!.name.trim()
            companyData.value!!.city = companyData.value!!.city.trim()
            companyData.value!!.email = companyData.value!!.email.trim()
            companyData.value!!.description = companyData.value!!.description.trim()
            companyData.value!!.neighborhood = companyData.value!!.neighborhood.trim()
        }
    }

    private fun validateCompanyModel(view: View): Boolean {
        val company: CompanyData = companyData.value!!
        val maxLengthNeighborhood = 35
        val maxLengthCity = 28

        var isValid = StringValidator(company.name)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.company_name_hint)
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
                        view.context.getString(R.string.company_segment_hint)
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
                        view.context.getString(R.string.company_description_hint)
                    )
                )
            )
            .addValidation(
                MaxLengthRule(
                    MAX_DESCRIPTION_SIZE,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.company_description_hint),
                        MAX_DESCRIPTION_SIZE
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
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.company_cep_hint)
                    )
                )
            )
            .addValidation(
                ValidCepRule(
                    view.context.getString(R.string.invalid_cep_error_message)
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_CEP,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.city)
            .addValidation(
                MaxLengthRule(
                    maxLengthCity,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.company_city_hint),
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
                MaxLengthRule(
                    maxLengthNeighborhood,
                    view.context.getString(
                        R.string.max_text_length_error_message,
                        view.context.getString(R.string.company_neighborhood_hint),
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

        isValid = isValid && (company.cnpj.isBlank() || StringValidator(company.cnpj)
            .addValidation(ValidCnpjRule(view.context.getString(R.string.invalid_cnpj_error_message)))
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_CNPJ,
                    it.error
                )
            }
            .validate())

        isValid = isValid and StringValidator(company.cellphone)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.company_telephone_hint)
                    )
                )
            )
            .addValidation(
                ValidTelephoneRule(
                    view.context.getString(R.string.invalid_telephone_error_message)
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_CELLPHONE,
                    it.error
                )
            }
            .validate()

        isValid = isValid and StringValidator(company.email)
            .addValidation(
                NotNullOrEmptyRule(
                    view.context.getString(
                        R.string.required_field_error_message,
                        view.context.getString(R.string.company_mail_hint)
                    )
                )
            )
            .addValidation(
                ValidEmailRule(
                    view.context.getString(
                        R.string.invalid_email_error_message
                    )
                )
            )
            .addErrorCallback {
                accountListener?.riseValidationError(
                    FieldsEnum.COMPANY_EMAIL,
                    it.error
                )
            }
            .validate()

        isValid = isValid &&
                !(!StringValidator(company.thumbnail.path)
                    .addValidation(NotNullOrEmptyRule(view.context.getString(R.string.required_image_error_message, view.context.getString(R.string.account_company_logo_hint))))
                    .addErrorCallback {
                        accountListener?.riseValidationError(
                            FieldsEnum.COMPANY_THUMBNAIL,
                            it.error
                        )
                    }.validate() ||
                !StringValidator(company.banner.path)
                    .addValidation(NotNullOrEmptyRule(view.context.getString(R.string.required_image_error_message, view.context.getString(R.string.account_company_promotional_photo_hint))))
                    .addErrorCallback {
                        accountListener?.riseValidationError(
                            FieldsEnum.COMPANY_BANNER,
                            it.error
                        )
                    }
                    .validate())

        return isValid
    }
    // endregion

    companion object {
        const val MAX_DESCRIPTION_SIZE = 144
    }
}