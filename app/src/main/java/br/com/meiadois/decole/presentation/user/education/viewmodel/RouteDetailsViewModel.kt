package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.util.lazyDeferred

class RouteDetailsViewModel(
    val repository: LessonRepository
) : ViewModel() {

    val routeClicked = MutableLiveData<Long>()

    val lessons by lazyDeferred {
        repository.getLessons(routeClicked.value!!)
    }

    fun onItemClick(item: Route, view: View) {

    }

}