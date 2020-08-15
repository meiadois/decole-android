package br.com.meiadois.decole.presentation.user.account.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountMenuViewModel
import br.com.meiadois.decole.service.LogOutService

class AccountMenuViewModelFactory(private val logOutService: LogOutService): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountMenuViewModel(logOutService) as T
    }
}