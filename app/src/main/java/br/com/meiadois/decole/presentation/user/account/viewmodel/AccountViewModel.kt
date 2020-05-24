package br.com.meiadois.decole.presentation.user.account.viewmodel

import android.service.autofill.UserData
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.network.response.CepResponse
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.network.response.UserUpdateResponse
import br.com.meiadois.decole.data.repository.CepRepository
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.account.binding.UserAccountData
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyAccountData
import br.com.meiadois.decole.util.extension.toSegmentModelList
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
    fun onCepFieldChange(cep: String) {
        if (cep.isNotBlank() && cep.length == 9) {
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
        if (validateModels()) {
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
                            getRequestBody(companyData.value!!.name),
                            getRequestBody(companyData.value!!.cep),
                            getRequestBody(companyData.value!!.cnpj),
                            getRequestBody(companyData.value!!.description),
                            getRequestBody(companyData.value!!.segmentId),
                            getRequestBody(companyData.value!!.cellphone),
                            getRequestBody(companyData.value!!.email),
                            getRequestBody(companyData.value!!.visible),
                            getRequestBody(companyData.value!!.city),
                            getRequestBody(companyData.value!!.neighborhood),
                            getMultipartBodyPart(companyData.value!!.thumbnail, "thumbnail"),
                            getMultipartBodyPart(companyData.value!!.banner, "banner")
                        ) else userRepository.insertUserCompany(
                            getRequestBody(companyData.value!!.name),
                            getRequestBody(companyData.value!!.cep),
                            getRequestBody(companyData.value!!.cnpj),
                            getRequestBody(companyData.value!!.description),
                            getRequestBody(companyData.value!!.segmentId),
                            getRequestBody(companyData.value!!.cellphone),
                            getRequestBody(companyData.value!!.email),
                            getRequestBody(companyData.value!!.visible),
                            getRequestBody(companyData.value!!.city),
                            getRequestBody(companyData.value!!.neighborhood),
                            getMultipartBodyPart(companyData.value!!.thumbnail, "thumbnail"),
                            getMultipartBodyPart(companyData.value!!.banner, "banner")
                        )
                } catch (ex: Exception) {
                    Log.i("AccountFormException", ex.message!!)
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

    private fun getRequestBody(value: Any): RequestBody = RequestBody.create(MultipartBody.FORM, value.toString())
    // endregion

    // region Validation
    private fun validateModels(): Boolean = validateUserModel() && validateCompanyModel()

    private fun validateUserModel(): Boolean {
        return true
    }

    private fun validateCompanyModel(): Boolean {
        return true
    }
    // endregion
}