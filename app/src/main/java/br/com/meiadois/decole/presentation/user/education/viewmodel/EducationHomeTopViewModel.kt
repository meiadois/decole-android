package br.com.meiadois.decole.presentation.user.education.viewmodel

import br.com.meiadois.decole.data.repository.MetricsRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Metrics
import br.com.meiadois.decole.util.extension.toMetrics

class EducationHomeTopViewModel(
    private val metricasRepository: MetricsRepository

) : ViewModel() {

    //suspend fun getUsermetrics():Metrics = metricasRepository.getUserMetricas().toMetrics()

}
