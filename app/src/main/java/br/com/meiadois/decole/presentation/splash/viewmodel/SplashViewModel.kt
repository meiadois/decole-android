package br.com.meiadois.decole.presentation.splash.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.network.response.AppInfoResponse
import br.com.meiadois.decole.data.repository.UserRepository

class SplashViewModel(private val userRepository: UserRepository) : ViewModel() {

    suspend fun getAppInfo(): AppInfoResponse = userRepository.getAppInfo()

    fun getLoggedInUser() = userRepository.getUser()
}