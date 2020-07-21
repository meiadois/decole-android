package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.presentation.user.education.StartInteractiveModeActivity
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.lazyDeferred
import kotlinx.android.synthetic.main.activity_route_details.*


class RouteDetailsViewModel(
    private val routeRepository: RouteRepository,
    private val lessonRepository: LessonRepository
) : ViewModel() {

    val routeClicked = MutableLiveData<Long>()

    val routeDetails by lazyDeferred {
        routeRepository.getRoute(routeClicked.value!!)
    }

    fun onItemClick(item: Lesson, view: View) {
        Intent(view.context, StartInteractiveModeActivity::class.java).also {
            it.putExtra("lessonId", item.id)
            it.putExtra("routeId", routeClicked.value!!)
            view.context.startActivity(it)
        }


    }

    suspend fun onJumpButtonClick() {
        routeRepository.jumpRoute(routeClicked.value!!)
        lessonRepository.fetchLessons(routeClicked.value!!)
        routeDetails.start()
    }

}