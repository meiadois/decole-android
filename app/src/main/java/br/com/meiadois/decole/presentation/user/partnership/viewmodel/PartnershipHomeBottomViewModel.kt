package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.*
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModel
import br.com.meiadois.decole.util.extension.toMatchItemList

class PartnershipHomeBottomViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val partnershipLiveData : MutableLiveData<List<Like>> = MutableLiveData()

    fun getPartnerships(companyId: Int){
        Coroutines.main {
            partnershipLiveData.value = userRepository.listUserMatches().toMatchItemList(companyId)
        }
    }

    fun removeLike(likeId: Int){
        partnershipLiveData.value = partnershipLiveData.value?.filter { like -> like.id != likeId }
    }

    suspend fun getUserCompany() : Company = userRepository.getUserCompany().toCompanyModel()
}