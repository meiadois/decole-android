package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.presentation.user.education.RouteDetailsActivity
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.lazyDeferred

class RouteListViewModel(
    private val routeRepository: RouteRepository,
    private val lessonRepository: LessonRepository
) : ViewModel() {

    val routes by lazyDeferred {
        routeRepository.getRoutes()
    }

    fun onItemClick(item: Route, view: View) {
        Coroutines.io {
            lessonRepository.fetchLessons(item.id)
        }
        Intent(view.context, RouteDetailsActivity::class.java).also {
            it.putExtra("itemId", item.id)
            view.context.startActivity(it)
        }
    }

    fun onListRefresh() = Coroutines.main {
        routeRepository.fetchRoutes()
    }
}