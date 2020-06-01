package br.com.meiadois.decole.presentation.user.account.binding

data class UserAccountData(
    var name: String = "",
    var email: String = ""
)

data class UserSocialNetworksData(
    var instagram: String = "",
    var facebook: String = ""
)