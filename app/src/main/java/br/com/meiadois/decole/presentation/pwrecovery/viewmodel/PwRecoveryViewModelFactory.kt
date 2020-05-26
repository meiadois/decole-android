package br.com.meiadois.decole.presentation.pwrecovery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.data.repository.UserRepository

class PwRecoveryViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (mViewModel == null) {
            val instance =
                PwRecoveryViewModel(userRepository)
            mViewModel = instance
        }
        return mViewModel as T
    }

    companion object {
        var mViewModel: PwRecoveryViewModel? = null
    }
}