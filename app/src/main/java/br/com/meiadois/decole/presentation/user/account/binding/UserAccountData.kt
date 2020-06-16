package br.com.meiadois.decole.presentation.user.account.binding

data class UserAccountData(
    var name: String = "",
    var email: String = "",
    var jwt: String = "",
    var introduced: Boolean = false
)

data class UserAccountsData(
    var instagram: UserSocialNetwork = UserSocialNetwork(),
    var facebook: UserSocialNetwork = UserSocialNetwork()
)

data class UserSocialNetwork(
    var userName: String = "",
    var channelName: String = "",
    var category: String = "",
    var id: Int = 0
)