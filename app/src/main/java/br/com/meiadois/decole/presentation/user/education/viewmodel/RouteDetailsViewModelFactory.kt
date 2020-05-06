package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.RouteRepository

class RouteDetailsViewModelFactory(
    private val repository: RouteRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RouteDetailsViewModel(repository) as T
    }
}