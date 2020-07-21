package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetricsResponse(

    @Json(name = "sucess")
    val sucess: Boolean,
    @Json(name = "error_message")
    val error_message: String,
    @Json(name = "value")
    val value: Float
)