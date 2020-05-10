package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteDTO(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
//    @Json(name = "locked") val locked: Boolean = false,
    @Json(name = "progress") val progress: ProgressDTO
)

@JsonClass(generateAdapter = true)
data class ProgressDTO(
    @Json(name = "done") val done: Int,
    @Json(name = "total") val total: Int,
    @Json(name = "remain") val remaining: Int,
    @Json(name = "percentage") val percentage: Int
)