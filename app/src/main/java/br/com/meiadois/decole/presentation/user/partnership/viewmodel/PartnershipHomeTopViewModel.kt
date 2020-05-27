package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModel
import br.com.meiadois.decole.util.extension.toMatchItemList

class PartnershipHomeTopViewModel (
    private val userRepository: UserRepository
    ) : ViewModel() {
        private val companyLiveData : MutableLiveData<Company> = MutableLiveData()

        suspend fun getUserCompany(): Company {
            return userRepository.getUserCompany().toCompanyModel()
        }

}
