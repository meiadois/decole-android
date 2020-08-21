package br.com.meiadois.decole.presentation.welcome.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.service.LogOutService

class WelcomeInfoViewModel(private val logOutService: LogOutService) : ViewModel() {
    fun onLogoutButtonClicked(context: Context) = logOutService.perform(context)
}