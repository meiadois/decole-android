package br.com.meiadois.decole.presentation.user.account.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.service.LogOutService

class AccountMenuViewModel(private val logOutService: LogOutService) : ViewModel() {
    suspend fun onLogOutButtonClick() = logOutService.perform().join()
}