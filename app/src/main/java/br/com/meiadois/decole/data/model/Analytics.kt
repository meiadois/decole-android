package br.com.meiadois.decole.data.model

data class Analytics(
    val mean_of_hashtags: Metrics,
    val mean_of_mentions: Metrics,
    val mean_of_comments: Metrics,
    val mean_of_likes: Metrics,
    val posts_with_hashtags: Metrics,
    val followers_per_following: Metrics,
    val followers: Metrics,
    val following: Metrics,
    val publications: Metrics
)