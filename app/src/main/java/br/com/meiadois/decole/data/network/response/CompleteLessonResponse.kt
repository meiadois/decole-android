package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompleteLessonResponse(
    @Json(name = "lesson_id")
    val doneLessonId: String

)