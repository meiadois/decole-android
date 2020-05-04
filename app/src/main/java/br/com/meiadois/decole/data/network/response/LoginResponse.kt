package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "message")
    val message: String?,
    @Json(name = "user")
    val user: UserDTO?
)

@JsonClass(generateAdapter = true)
data class UserDTO(
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "token")
    val jwt: String
)