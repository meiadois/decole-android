package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.repository.MetricsRepository
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Analytics
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.user.account.binding.UserSocialNetworksData
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModel
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toAnalytics

class EducationHomeTopViewModel(
    private val metricasRepository: MetricsRepository,
    private val userRepository: UserRepository

) : ViewModel() {

    suspend fun getUsermetrics(): Analytics = metricasRepository.getUserMetricas().toAnalytics()

    suspend fun getUserSocialNetworks(): Boolean {
        try {
            val userSocialNetworksData = UserSocialNetworksData()
            userRepository.getUserAccounts().map {
                if (it.channel!!.name == "Instagram") {
                    userSocialNetworksData.instagram = it.username
                }
            }
            if (userSocialNetworksData.instagram.isNotEmpty())
                return true
        } catch (ex: Exception) {
            Log.i("EducationTop.init", ex.message ?: "no error message")
            return false
        }
        return false
    }

}


