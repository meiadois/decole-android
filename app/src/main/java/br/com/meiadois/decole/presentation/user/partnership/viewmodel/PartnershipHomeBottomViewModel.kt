package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.*
import br.com.meiadois.decole.data.model.Partner
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.repository.UserRepository

class PartnershipHomeBottomViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val partnershipLiveData : MutableLiveData<List<Partner>> = MutableLiveData()

    fun getPartnerships(){ }

    suspend fun getUserCompanies() : List<CompanyResponse> = userRepository.listUserCompanies()
}