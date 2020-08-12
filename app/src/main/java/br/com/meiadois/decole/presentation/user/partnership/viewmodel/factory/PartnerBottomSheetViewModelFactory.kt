package br.com.meiadois.decole.presentation.user.partnership.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnerBottomSheetViewModel

class PartnerBottomSheetViewModelFactory(
    private val companyRepository: CompanyRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PartnerBottomSheetViewModel(
            companyRepository
        ) as T
    }
}