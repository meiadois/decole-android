package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.SegmentRepository

class PartnershipCompanyProfileViewModelFactory(
    private val companyRepository: CompanyRepository,
    private val segmentRepository: SegmentRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PartnershipCompanyProfileViewModel(companyRepository, segmentRepository ) as T
    }
}