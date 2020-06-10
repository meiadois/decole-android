package br.com.meiadois.decole.data.network.response

import br.com.meiadois.decole.data.model.Metrics
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalyticsResponse(
    @Json(name = "mean_of_hashtags")
    val mean_of_hashtags: MetricsResponse,
    @Json(name = "mean_of_mentions")
    val mean_of_mentions: MetricsResponse,
    @Json(name = "mean_of_comments")
    val mean_of_comments: MetricsResponse,
    @Json(name = "mean_of_likes")
    val mean_of_likes: MetricsResponse,
    @Json(name = "posts_with_hashtags")
    val posts_with_hashtags: MetricsResponse,
    @Json(name = "followers_per_following")
    val followers_per_following: MetricsResponse,
    @Json(name = "followers")
    val followers: MetricsResponse,
    @Json(name = "following")
    val following: MetricsResponse,
    @Json(name = "publications")
    val publications: MetricsResponse
)