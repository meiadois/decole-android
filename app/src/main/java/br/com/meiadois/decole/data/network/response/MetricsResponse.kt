package br.com.meiadois.decole.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetricsResponse(
    @Json(name = "mean_of_hashtags.value")
    val mean_of_hashtags: Float,
    @Json(name = "mean_of_mentions.value")
    val mean_of_mentions: Float,
    @Json(name = "mean_of_comments.value")
    val mean_of_comments: Float,
    @Json(name = "mean_of_likes.value")
    val mean_of_likes: Float,
    @Json(name = "posts_with_hashtags.value")
    val posts_with_hashtags: Int,
    @Json(name = "followers_per_following.value")
    val followers_per_following: Float,
    @Json(name = "followers.value")
    val followers: Int,
    @Json(name = "following.value")
    val following: Int,
    @Json(name = "publications.value")
    val publications: Int

)