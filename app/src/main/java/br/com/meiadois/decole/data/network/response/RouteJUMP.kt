package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteJUMP(
    @Json(name = "id")
    val jump_id: String,
    @Json(name = "route_id")
    val jump_idroute: String

)