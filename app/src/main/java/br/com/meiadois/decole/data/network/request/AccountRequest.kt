package br.com.meiadois.decole.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountRequest(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "channel_name") val channel: String = ""
)