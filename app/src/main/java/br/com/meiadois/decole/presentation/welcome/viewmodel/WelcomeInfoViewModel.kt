package br.com.meiadois.decole.presentation.welcome.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.service.LogOutService

class WelcomeInfoViewModel(
    private val logOutService: LogOutService
) : ViewModel() {

    /** Essa task roda em background */
    fun onLogoutButtonClicked() {
        logOutService.perform()
    }
}