package com.aghourservices.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: Int,
    val title: String,
    val body: String,
    val user: User? = null,
    @SerializedName("notifiable_id")
    val notifiableId: Int,
    @SerializedName("notifiable_type")
    val notifiableType: String,
    @SerializedName("article_id")
    val articleId: Int? = null,
    @SerializedName("image_url")
    val articleImageUrl: String,
    @SerializedName("created_at")
    val timeAgo: String,
)