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
    @Json(name = "cnpj")
    val cnpj: String,
    @Json(name = "segment_id")
    val segmentId: Int,
    @Json(name = "segment")
    val segment: SegmentResponse?
)