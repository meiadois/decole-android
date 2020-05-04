package br.com.meiadois.decole.data.http.response

import br.com.meiadois.decole.data.model.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "user")
    val user: User?,
    @Json(name = "message")
    val message: String?
)