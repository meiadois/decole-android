package br.com.meiadois.decole.presentation.user.education.viewmodel

import br.com.meiadois.decole.data.repository.MetricsRepository
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Analytics
import br.com.meiadois.decole.util.extension.toAnalytics

class EducationHomeTopViewModel(
    private val metricasRepository: MetricsRepository

) : ViewModel() {

    suspend fun getUsermetrics(): Analytics = metricasRepository.getUserMetricas().toAnalytics()

}
