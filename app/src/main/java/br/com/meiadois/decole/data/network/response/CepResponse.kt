package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CepResponse(
    @Json(name = "cep")
    val cep: String,
    @Json(name = "state")
    val state: String,
    @Json(name = "city")
    val city: String,
    @Json(name = "neighborhood")
    val neighborhood: String,
    @Json(name = "street")
    val street: String
)