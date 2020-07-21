package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IntroduceResponse(
    @Json(name = "name")
    val userName: String?,
    @Json(name = "email")
    val userEmail: String?
)