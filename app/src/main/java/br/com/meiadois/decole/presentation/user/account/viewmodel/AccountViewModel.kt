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
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.network.response.UserUpdateResponse
import br.com.meiadois.decole.data.repository.CepRepository
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.AccountListener
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum
import br.com.meiadois.decole.presentation.user.account.binding.UserAccountData
import br.com.meiadois.decole.presentation.user.account.validation.*
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyAccountData
import br.com.meiadois.decole.util.extension.toSegmentModelList
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class AccountViewModel(
    private val segmentRepository: SegmentRepository,
    private val userRepository: UserRepository,
    private val cepRepository: CepRepository
) : ViewModel() {
    var companyData: MutableLiveData<CompanyAccountData> = MutableLiveData<CompanyAccountData>()
    var userData: MutableLiveData<UserAccountData> = MutableLiveData<UserAccountData>()
    var segments: MutableLiveData<List<Segment>> = MutableLiveData<List<Segment>>()
    var accountListener: AccountListener? = null
    private var isUpdating: Boolean = false

    // region initializer methods
    init {
        getUserCompany()
        getSegments()
        getUser()
    }

    private fun getSegments() {
        Coroutines.main {
            try {
                segments.value = segmentRepository.getAllSegments().toSegmentModelList()
            } catch (ex: Exception) {
                Log.i("AccountViewModel.init", ex.message!!)
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
            Log.i("AccountViewModel.init", ex.message!!)
        }
    }

    private fun getUserCompany() {
        Coroutines.main {
            try {
                companyData.value = userRepository.getUserCompany().toCompanyAccountData()
                isUpdating = true
            } catch (ex: Exception) {
                companyData.value = CompanyAccountData()
                Log.i("AccountViewModel.init", ex.message!!)
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
                    companyData.value?.neighborhood = cepResponse.neighborhood
                    companyData.value?.city = cepResponse.city
                } catch (ex: Exception) {
                    Log.i("CepException", ex.message!!)
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
        if (validateModels(view)) {
            Coroutines.main {
                try {
                    val userResponse: UserUpdateResponse =
                        userRepository.updateUser(userData.value!!.name, userData.value!!.email)

                    /*
                        Etapa para inserir ou editar a empresa do usuário.
                        O getRequestBody vai montar um objeto do tipo RequestBody
                        O getMultipartBody_Part vai montar um objeto do tipo MultipartBody.Part com a imagem

                        link com um converter para user tipo primitivos junto com arquivo
                        [https://stackoverflow.com/a/47627526/10457149]

                        link que guilherme mandou para upload de aquivos
                        [https://www.journaldev.com/23709/android-image-uploading-with-retrofit-and-node-js]

                        um outro link também com upload de arquivos
                        [https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server]

                        outro link de upload aí
                        [https://stackoverflow.com/questions/34562950/post-multipart-form-data-using-retrofit-2-0-including-image]
                     */
                    val companyResponse: CompanyResponse =
                        if (isUpdating) userRepository.updateUserCompany(
                            companyData.value!!.name,
                            companyData.value!!.cep,
                            companyData.value!!.cnpj,
                            companyData.value!!.description,
                            companyData.value!!.segmentId,
                            companyData.value!!.cellphone,
                            companyData.value!!.email,
                            companyData.value!!.visible,
                            companyData.value!!.city,
                            companyData.value!!.neighborhood
                        ) else userRepository.insertUserCompany(
                            companyData.value!!.name,
                            companyData.value!!.cep,
                            companyData.value!!.cnpj,
                            companyData.value!!.description,
                            companyData.value!!.segmentId,
                            companyData.value!!.cellphone,
                            companyData.value!!.email,
                            companyData.value!!.visible,
                            companyData.value!!.city,
                            companyData.value!!.neighborhood
                        )
                } catch (ex: Exception) {
                    Log.i("AccountFormException", "Deu errado" + ex.message!!)
                }
            }
        }
    }

    private fun getMultipartBodyPart(imagePath: String, parameterName: String): MultipartBody.Part {
        val file = File(imagePath)
        return MultipartBody.Part.createFormData(
            parameterName,
            file.name,
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                file
            )
        )
    }

    private fun getRequestBody(value: Any): RequestBody =
        RequestBody.create(MultipartBody.FORM, value.toString())
    // endregion

    // region Validation
    private fun validateModels(view: View): Boolean {
        val isValid = validateUserModel(view)
        return validateCompanyModel(view) and isValid
    }


    private fun validateUserModel(view: View): Boolean {
        val user: UserAccountData = userData.value!!

        var isValid = StringValidator(user.name)
            .addValidation(NotNullOrEmptyRule(
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_me_name_hint))))
            .addErrorCallback { accountListener?.riseError(FieldsEnum.USER_NAME, it.error) }
            .validate()

        isValid = isValid and StringValidator(user.email)
            .addValidation(ValidEmailRule(view.context.getString(R.string.invalid_email_error_message)))
            .addErrorCallback { accountListener?.riseError(FieldsEnum.USER_EMAIL, it.error) }
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
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_NAME, it.error) }
            .validate()

        isValid = isValid and IntegerValidator(company.segmentId)
            .addValidation(MinRule(1,
                view.context.getString(
                    R.string.required_field_error_message,
                    view.context.getString(R.string.account_company_segment_hint))))
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_SEGMENT, it.error) }
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
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_DESCRIPTION, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.cep)
            .addValidation(ValidCepRule(view.context.getString(R.string.invalid_cep_error_message)))
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_CEP, it.error) }
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
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_CITY, it.error) }
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
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_NEIGHBORHOOD, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.cnpj)
            .addValidation(ValidCnpjRule(view.context.getString(R.string.invalid_cnpj_error_message)))
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_CNPJ, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.cellphone)
            .addValidation(ValidTelephoneRule(view.context.getString(R.string.invalid_telephone_error_message)))
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_CELLPHONE, it.error) }
            .validate()

        isValid = isValid and StringValidator(company.email)
            .addValidation(ValidEmailRule(view.context.getString(R.string.invalid_email_error_message)))
            .addErrorCallback { accountListener?.riseError(FieldsEnum.COMPANY_EMAIL, it.error) }
            .validate()

        return isValid
    }
    // endregion
}