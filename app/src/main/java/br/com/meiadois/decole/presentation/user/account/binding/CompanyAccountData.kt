package br.com.meiadois.decole.presentation.user.account.binding

data class CompanyAccountData(
    var id: Int = -1,
    var name: String = "",
    var cep: String = "",
    var thumbnail: String = "",
    var banner: String = "",
    var cnpj: String = "",
    var cellphone: String = "",
    var email: String = "",
    var description: String = "",
    var visible: Boolean = false,
    var city: String? = null,
    var neighborhood: String? = null,
    var segmentId: Int = -1,
    var segmentName: String? = ""
)