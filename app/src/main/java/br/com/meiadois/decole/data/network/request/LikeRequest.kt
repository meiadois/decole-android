package br.com.meiadois.decole.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LikeRequest(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "sender_id") val senderId: Int,
    @field:Json(name = "recipient_id") val recipientId: Int
)

@JsonClass(generateAdapter = true)
data class LikeSenderRequest(
    @field:Json(name = "sender_id") val senderId: Int,
    @field:Json(name = "recipient_id") val recipientId: Int
)