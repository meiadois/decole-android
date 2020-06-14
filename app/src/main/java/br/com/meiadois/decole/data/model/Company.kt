package br.com.meiadois.decole.data.model

class Company(
    val id: Int = 0,
    val name: String = "",
    val cep: String = "",
    val thumbnail: String = "",
    val banner: String = "",
    val cnpj: String = "",
    val cellphone: String = "",
    val email: String = "",
    val description: String = "",
    val visible: Boolean = false,
    val city: String? = "",
    val neighborhood: String? = "",
    val segment: Segment? = null
)

