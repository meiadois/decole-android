package br.com.meiadois.decole.presentation.user.education.viewmodel.factory

import MetricsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.presentation.user.education.viewmodel.EducationHomeTopViewModel

class EducationHomeTopViewModelFactory(
    private val metricsRepository: MetricsRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EducationHomeTopViewModel(metricsRepository) as T
    }
}