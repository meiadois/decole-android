package br.com.meiadois.decole.presentation.splash.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.repository.UserRepository

class SplashViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getLoggedInUser() = userRepository.getUser()
}