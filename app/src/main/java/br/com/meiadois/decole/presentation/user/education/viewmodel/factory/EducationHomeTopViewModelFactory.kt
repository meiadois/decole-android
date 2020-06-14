package br.com.meiadois.decole.presentation.user.education.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.MetricsRepository
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.education.viewmodel.EducationHomeTopViewModel

class EducationHomeTopViewModelFactory(
    private val metricsRepository: MetricsRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EducationHomeTopViewModel(metricsRepository, userRepository) as T
    }
}