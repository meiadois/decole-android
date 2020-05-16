package br.com.meiadois.decole.presentation.user.account.binding

data class CompanyAccountData(
    var shownInPartnersResearch: Boolean = false,
    var name : String = "",
    var segmentId: Int = -1,
    var description: String = "",
    var cep: String = "",
    var cnpj: String = "",
    var telephone: String = "",
    var email: String = ""
)