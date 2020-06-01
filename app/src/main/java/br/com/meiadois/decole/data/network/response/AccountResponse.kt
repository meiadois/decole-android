package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "username")
    val username: String,
    @Json(name = "channel")
    val channel: ChannelResponse?
)

@JsonClass(generateAdapter = true)
data class ChannelResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "category")
    val category: String
)