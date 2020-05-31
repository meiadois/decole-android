package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModel

class PartnershipHomeTopViewModel (
    private val companyRepository: CompanyRepository
    ) : ViewModel() {

        suspend fun getUserCompany(): LiveData<MyCompany> {
            val res = companyRepository.getMyCompany()
            var a = 1
            return res
        }
}
