package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LikeResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "status")
    val status: String,
    @Json(name = "sender_id")
    val sender_id: Int,
    @Json(name = "recipient_id")
    val recipient_id: Int,
    @Json(name = "sender_company")
    val sender_company: CompanyResponse,
    @Json(name = "recipient_company")
    val recipient_company: CompanyResponse
)