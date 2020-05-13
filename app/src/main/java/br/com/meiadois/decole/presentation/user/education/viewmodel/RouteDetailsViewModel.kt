package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.data.repository.StepRepository
import br.com.meiadois.decole.presentation.user.education.StartInteractiveModeActivity
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.lazyDeferred


class RouteDetailsViewModel(
    private val stepRepository: StepRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    val routeClicked = MutableLiveData<Long>()

    val steps = MutableLiveData<List<Step>>()

    val routeDetails by lazyDeferred {
        routeRepository.getRoute(routeClicked.value!!)
    }

    fun onItemClick(item: Lesson, view: View) = Coroutines.main {
        Intent(view.context, StartInteractiveModeActivity::class.java).also {
            it.putExtra("lessonId", item.id)
            view.context.startActivity(it)
        }
    }


}