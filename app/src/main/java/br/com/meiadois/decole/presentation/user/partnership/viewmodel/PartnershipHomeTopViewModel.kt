package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.repository.CompanyRepository

class PartnershipHomeTopViewModel(
    private val companyRepository: CompanyRepository
) : ViewModel() {

    suspend fun getUserCompany(): LiveData<MyCompany> = companyRepository.getMyCompany()

}
