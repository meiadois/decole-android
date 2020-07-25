package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.data.repository.StepRepository
import br.com.meiadois.decole.presentation.user.education.InteractiveModeListener
import br.com.meiadois.decole.util.Coroutines
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class StartInteractiveModeViewModel(
    private val stepRepository: StepRepository
) : ViewModel() {

    var interactiveListener: InteractiveModeListener? = null
    val lessonClicked = MutableLiveData<Long>()

    var steps = MutableLiveData<List<Step>>()

    fun getSteps() {
        Coroutines.main {
            interactiveListener?.onStarted()
            try {
                stepRepository.getSteps(lessonClicked.value!!).observeForever{
                    steps.postValue(it)
                }
            } catch (ex: Exception) {
                Firebase.crashlytics.recordException(ex)
                interactiveListener?.onFailure(ex)
            }
        }
    }
}