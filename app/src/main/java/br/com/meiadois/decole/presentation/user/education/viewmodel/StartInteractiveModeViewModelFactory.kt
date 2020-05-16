package br.com.meiadois.decole.presentation.user.education.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.StepRepository

class StartInteractiveModeViewModelFactory(
    private val stepRepository: StepRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StartInteractiveModeViewModel(stepRepository) as T
    }
}