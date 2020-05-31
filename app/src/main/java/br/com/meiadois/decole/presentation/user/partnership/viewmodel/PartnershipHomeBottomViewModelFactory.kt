package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteDetailsViewModel

class PartnershipHomeBottomViewModelFactory(
    private val userRepository: UserRepository,
    private val companyRespository: CompanyRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PartnershipHomeBottomViewModel(userRepository, companyRespository) as T
    }
}