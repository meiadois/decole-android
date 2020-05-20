package br.com.meiadois.decole.presentation.user.account.binding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

data class UserAccountData(
    @Bindable
    var name : String = "",
    @Bindable
    var email : String = "",
    var newPassword : String = "",
    var confirmPassword : String = ""
) : BaseObservable()