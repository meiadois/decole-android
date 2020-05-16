package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.*
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.network.response.LikeResponse
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.util.Coroutines

class PartnershipHomeBottomViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val partnershipLiveData : MutableLiveData<List<LikeResponse>> = MutableLiveData()

    fun getPartnerships(companyId: Int){
        Coroutines.main {
            partnershipLiveData.value = userRepository.listUserMatches(companyId)
        }
    }

    suspend fun getUserCompany() : CompanyResponse = userRepository.getUserCompany()
}