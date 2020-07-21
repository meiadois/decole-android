package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.CompanyRepository

class PartnerBottomSheetViewModelFactory(
    private val companyRepository: CompanyRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PartnerBottomSheetViewModel(companyRepository) as T
    }
}