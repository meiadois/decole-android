package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteJumpResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "route_id")
    val jumpedRouteId: String

)