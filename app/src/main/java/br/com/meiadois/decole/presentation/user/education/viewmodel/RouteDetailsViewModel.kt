package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.presentation.user.education.StartInteractiveModeActivity
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.lazyDeferred


class RouteDetailsViewModel(
    private val routeRepository: RouteRepository,
    private val lessonRepository: LessonRepository
) : ViewModel() {

    val routeClicked = MutableLiveData<Long>()

    val routeDetails by lazyDeferred {
        routeRepository.getRoute(routeClicked.value!!)

    }

    fun onItemClick(item: Lesson, view: View) = Coroutines.main {
        Intent(view.context, StartInteractiveModeActivity::class.java).also {
            it.putExtra("lessonId", item.id)
            it.putExtra("routeId", routeClicked.value!!)
            view.context.startActivity(it)
        }
    }

    fun onJumpButtonClick() {

        Coroutines.main {
            routeRepository.jumpRoute(routeClicked.value!!)
            lessonRepository.fetchLessons(routeClicked.value!!)
            routeDetails.start()
        }
    }

    fun handleStart(lessonDone: Long) {
        if (lessonDone != 0L) {
            Coroutines.main {
                lessonRepository.complete(lessonDone)
                lessonRepository.fetchLessons(routeClicked.value!!)
                routeRepository.fetchRoute(routeClicked.value!!)
            }
        }
    }


}