package br.com.meiadois.decole.presentation.user.account.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.account.binding.UserAccountData

class AccountViewModel : ViewModel() {
    val userData: UserAccountData = UserAccountData()
    val companyData: CompanyAccountData = CompanyAccountData()

    fun onSaveButtonClick(view: View){

    }

    fun onSearchVisibilityChange(checked: Boolean){
        companyData.shownInPartnersResearch = checked
    }
}