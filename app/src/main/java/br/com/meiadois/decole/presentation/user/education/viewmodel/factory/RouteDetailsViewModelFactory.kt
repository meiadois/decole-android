package br.com.meiadois.decole.presentation.user.education.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.presentation.user.education.viewmodel.RouteDetailsViewModel

class RouteDetailsViewModelFactory(
    private val routeRepository: RouteRepository,
    private val lessonRepository: LessonRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RouteDetailsViewModel(
            routeRepository,
            lessonRepository
        ) as T
    }
}