package br.com.meiadois.decole.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompanyRequest(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "segment_id") val segmentId: Int,
    @field:Json(name = "cnpj") val cnpj: String,
    @field:Json(name = "cellphone") val cellphone: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "cep") val cep: String,
    @field:Json(name = "city") val city: String,
    @field:Json(name = "neighborhood") val neighborhood: String,
    @field:Json(name = "visible") val visible: Boolean,
    @field:Json(name = "thumbnail") val thumbnail: String,
    @field:Json(name = "banner") val banner: String
)