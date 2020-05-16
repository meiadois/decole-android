package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompanyResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "cep")
    val cep: String,
    @Json(name = "thumbnail")
    val thumbnail: String,
    @Json(name = "banner")
    val banner: String,
    @Json(name = "cnpj")
    val cnpj: String,
    @Json(name = "cellphone")
    val cellphone: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "visible")
    val visible: Boolean,
    @Json(name = "city")
    val city: String,
    @Json(name = "neighborhood")
    val neighborhood: String,
    @Json(name = "state")
    val state: String,
    @Json(name = "street")
    val street: String,
    @Json(name = "segment")
    val segment: SegmentResponse?
)