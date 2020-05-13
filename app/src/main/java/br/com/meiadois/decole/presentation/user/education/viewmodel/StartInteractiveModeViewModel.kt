package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.repository.StepRepository
import br.com.meiadois.decole.util.lazyDeferred

class StartInteractiveModeViewModel(
    private val stepRepository: StepRepository
) : ViewModel() {

    val lessonClicked = MutableLiveData<Long>()

    val steps by lazyDeferred {
        stepRepository.getSteps(lessonClicked.value!!)
    }

}