package br.com.meiadois.decole.data.model

data class Like(
    val id: Int,
    val status: String,
    val partnerCompany: Company,
    val userCompany: Company,
    val isSender: Boolean = false
)