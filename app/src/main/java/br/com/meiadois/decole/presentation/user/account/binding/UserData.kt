package br.com.meiadois.decole.presentation.user.account.binding

data class UserData(
    var name: String = "",
    var email: String = "",
    var jwt: String = "",
    var introduced: Boolean = false
)