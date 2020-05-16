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
        // TODO: fix the route called here, maybe you will need to change a parameter or the entire route
        //  (need to search for matches that the recipient or sender is the current user company)
        Coroutines.main {
            partnershipLiveData.value = userRepository.listUserMatches(companyId).toMatchItemList(companyId)
        }
    }

    suspend fun getUserCompany() : Company = userRepository.getUserCompany().toCompanyModel()
}