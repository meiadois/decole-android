package br.com.meiadois.decole.data.model

data class Company(
    val id: Int,
    val name: String,
    val cep: String,
    val thumbnail: String,
    val banner: String,
    val cnpj: String,
    val cellphone: String,
    val email: String,
    val description: String,
    val visible: Boolean,
    val city: String?,
    val neighborhood: String?,
    val segment: Segment?
)

