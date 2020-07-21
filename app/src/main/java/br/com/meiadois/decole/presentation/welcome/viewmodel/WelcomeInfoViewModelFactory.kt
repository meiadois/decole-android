package br.com.meiadois.decole.presentation.welcome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.service.LogOutService

class WelcomeInfoViewModelFactory(
    private val logOutService: LogOutService
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WelcomeInfoViewModel(logOutService) as T
    }
}