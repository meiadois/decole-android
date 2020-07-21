package br.com.meiadois.decole.presentation.user.education.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.AccountRepository
import br.com.meiadois.decole.data.repository.MetricsRepository
import br.com.meiadois.decole.presentation.user.education.viewmodel.EducationHomeTopViewModel

class EducationHomeTopViewModelFactory(
    private val metricsRepository: MetricsRepository,
    private val accountRepository: AccountRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EducationHomeTopViewModel(metricsRepository, accountRepository) as T
    }
}