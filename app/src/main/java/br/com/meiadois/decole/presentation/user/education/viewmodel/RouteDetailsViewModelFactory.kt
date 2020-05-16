package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.data.repository.StepRepository

class RouteDetailsViewModelFactory(
    private val routeRepository: RouteRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RouteDetailsViewModel(routeRepository) as T
    }
}