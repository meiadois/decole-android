package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.repository.LessonRepository
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.presentation.user.education.IMResultListener
import br.com.meiadois.decole.util.Coroutines

class FinishedRouteViewModel(
    private val routeRepository: RouteRepository,
    private val lessonRepository: LessonRepository
) : ViewModel() {

    val targetRouteParent = MutableLiveData<Long>()

    var listener: IMResultListener? = null

    fun handleStart(lessonDone: Long) = Coroutines.main {
        if (lessonDone != 0L) {
            try {
                listener!!.onStarted()
                lessonRepository.complete(lessonDone)
                lessonRepository.fetchLessons(targetRouteParent.value!!)
                routeRepository.fetchRoute(targetRouteParent.value!!)
                listener!!.onSuccess()
            } catch (ex: Exception) {
                listener!!.onFailure(ex)
            }
        } else
            listener!!.onFailure(null)
    }
}