package br.com.meiadois.decole.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompanyBySegmentRequest(
    @field:Json(name = "segment_id") val segmentId: Int
)