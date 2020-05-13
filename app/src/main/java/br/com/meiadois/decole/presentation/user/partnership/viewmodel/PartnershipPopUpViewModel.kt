package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.network.response.SegmentResponse
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.util.Coroutines

class PartnershipPopUpViewModel(
    private val companyRepository: CompanyRepository
) : ViewModel() {
    val companyLiveData : MutableLiveData<CompanyResponse> = MutableLiveData()

    fun getCompanyById(companyId: Int) {
        Coroutines.main {
            companyLiveData.value = companyRepository.getCompanyById(companyId)
        }
    }
}