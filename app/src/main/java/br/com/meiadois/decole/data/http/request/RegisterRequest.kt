package br.com.meiadois.decole.data.http.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(@field:Json(name = "username") val username: String,
                           @field:Json(name = "email") val email: String,
                           @field:Json(name = "password") val password: String)