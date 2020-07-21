package br.com.meiadois.decole.presentation.welcome.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.repository.UserRepository

class WelcomeSlideViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun introduce() {
        userRepository.introduce()
    }
}