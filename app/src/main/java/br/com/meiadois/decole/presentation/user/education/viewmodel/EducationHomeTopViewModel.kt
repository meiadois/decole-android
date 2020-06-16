package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.util.Log
import br.com.meiadois.decole.data.repository.MetricsRepository
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Analytics
import br.com.meiadois.decole.data.repository.AccountRepository
import br.com.meiadois.decole.util.extension.toAnalytics

class EducationHomeTopViewModel(
    private val metricsRepository: MetricsRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    suspend fun getUserMetrics(): Analytics = metricsRepository.getUserMetrics().toAnalytics()

    suspend fun userHasInstagram(): Boolean {
        try {
            accountRepository.getUserAccounts().let { accounts ->
                for (account in accounts)
                    if (account.channelName == "Instagram")
                        return true
            }
        } catch (ex: Exception) {
            Log.i("EducationTop.init", ex.message ?: "no error message")
            return false
        }
        return false
    }
}


