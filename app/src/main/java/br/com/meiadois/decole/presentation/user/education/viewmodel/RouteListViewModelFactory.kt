package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.data.repository.RouteRepository

class RouteListViewModelFactory(
    private val repository: RouteRepository,
    private val lessonRepository: LessonRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RouteListViewModel(repository, lessonRepository) as T
    }
}