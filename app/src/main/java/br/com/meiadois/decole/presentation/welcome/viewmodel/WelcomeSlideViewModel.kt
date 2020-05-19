package br.com.meiadois.decole.presentation.welcome.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.util.Coroutines

class WelcomeSlideViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun introduce() = Coroutines.io {
        userRepository.introduce()
    }
}