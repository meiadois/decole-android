package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.data.repository.StepRepository
import br.com.meiadois.decole.presentation.user.education.InteractiveModeListener
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.exception.NoInternetException

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
                Log.i("Steps", "ENTROU")
            } catch (ex: NoInternetException) {
                interactiveListener?.onFailure(ex)
            } catch (ex: ClientException) {
                interactiveListener?.onFailure(ex)
            } catch (ex: Exception) {
                interactiveListener?.onFailure(ex)
            }
        }
    }
}