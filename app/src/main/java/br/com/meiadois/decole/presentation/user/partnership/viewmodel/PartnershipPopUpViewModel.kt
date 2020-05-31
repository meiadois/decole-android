package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.network.response.CompanyResponse
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

    suspend fun deletePartnership(likeId: Int, senderId: Int, recipientId: Int) = companyRepository.deletePartnership(likeId, senderId, recipientId)

    suspend fun deleteLike(likeId: Int) = companyRepository.deleteLike(likeId)

    suspend fun confirmPartnership(likeId: Int, senderId: Int, recipientId: Int) = companyRepository.confirmPartnership(likeId, senderId, recipientId)

    suspend fun cancelPartnership(likeId: Int, senderId: Int, recipientId: Int) = companyRepository.cancelPartnership(likeId, senderId, recipientId)
}