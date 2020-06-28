package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.data.repository.StepRepository
import br.com.meiadois.decole.presentation.user.education.InteractiveModeListener
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException
import br.com.meiadois.decole.util.lazyDeferred

class StartInteractiveModeViewModel(
    private val stepRepository: StepRepository
) : ViewModel() {

    var interactiveListener: InteractiveModeListener? = null
    val lessonClicked = MutableLiveData<Long>()

    val steps by lazyDeferred {
        interactiveListener?.onStarted()
        try {
            stepRepository.getSteps(lessonClicked.value!!)
        } catch (ex: NoInternetException){
            interactiveListener?.onFailure(ex)
            return@lazyDeferred MutableLiveData<List<Step>>()
        } catch (ex: ClientException) {
            interactiveListener?.onFailure(ex)
            return@lazyDeferred MutableLiveData<List<Step>>()
        } catch (ex: Exception){
            interactiveListener?.onFailure(ex)
            return@lazyDeferred MutableLiveData<List<Step>>()
        }
    }

}