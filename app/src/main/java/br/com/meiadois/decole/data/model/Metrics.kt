package br.com.meiadois.decole.data.model

data class Metrics(
    val mean_of_hashtags: Float,
    val mean_of_mentions: Float,
    val mean_of_comments: Float,
    val mean_of_likes: Float,
    val posts_with_hashtags: Int,
    val followers_per_following: Float,
    val followers: Int,
    val following: Int,
    val publications: Int
)