package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteDetailsResponse(
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "progress")
    val progress: ProgressDTO,
    @Json(name = "lessons")
    val lessons: List<LessonDTO>
)

@JsonClass(generateAdapter = true)
data class LessonDTO(
    @Json(name = "id")
    val id: Long,
    @Json(name = "title")
    val title: String,
    @Json(name = "done")
    val completed: Boolean
)