package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.*
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.network.response.SegmentResponse
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.util.Coroutines

class PartnershipHomeBottomViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val partnershipLiveData : MutableLiveData<List<CompanyResponse>> = MutableLiveData()

    fun getPartnerships(){
        /* TODO discomment this when api endpoint is ended
        Coroutines.io {
            partnershipLiveData.value = userRepository.listUserMatches()
        }*/
        partnershipLiveData.value = listOf(
            CompanyResponse(1, "Company 1", "00.00-00", "https://facebookbrand.com/wp-content/uploads/2019/04/f_logo_RGB-Hex-Blue_512.png?w=512&h=512","Descricao 1", "000.000.00-00", SegmentResponse("Segmento 1")),
            CompanyResponse(2, "Company 2", "00.00-00", "https://seeklogo.com/images/R/Roche-logo-A80FCF9553-seeklogo.com.png", "Descricao 1", "000.000.00-00", SegmentResponse("Segmento 2")),
            CompanyResponse(3, "Company 3", "00.00-00", "https://loucoporandroid.com/wp-content/uploads/2015/09/uber.png","Descricao 1", "000.000.00-00", SegmentResponse("Segmento 3"))
        )
    }

    suspend fun getUserCompanies() : List<CompanyResponse> = userRepository.listUserCompanies()
}