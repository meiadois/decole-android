package br.com.meiadois.decole.presentation.user.account.binding

data class UserAccountData(
    val name : String = "",
    val email : String = "",
    val newPassword : String = "",
    val confirmPassword : String = ""
)