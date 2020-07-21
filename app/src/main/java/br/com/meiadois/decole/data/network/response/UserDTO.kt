package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO(
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "token")
    val jwt: String,
    @Json(name = "introduced")
    val introduced: Boolean
)

@JsonClass(generateAdapter = true)
data class UserUpdateResponse(
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String
)