package br.com.meiadois.decole.presentation.user.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.*
import br.com.meiadois.decole.service.LogOutService

class AccountViewModelFactory(
    private val accountRepository: AccountRepository,
    private val segmentRepository: SegmentRepository,
    private val companyRepository: CompanyRepository,
    private val userRepository: UserRepository,
    private val cepRepository: CepRepository,
    private val logOutService: LogOutService
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountViewModel(accountRepository, segmentRepository, companyRepository, userRepository, cepRepository, logOutService) as T
    }
}