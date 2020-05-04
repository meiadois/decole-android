package br.com.meiadois.decole.data.http.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "token")
    val jwt: String?,
    @Json(name = "message")
    val message: String?
)