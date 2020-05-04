package br.com.meiadois.decole.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class User(
        @Json(name = "email")
        val email: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "token")
        val token: String)