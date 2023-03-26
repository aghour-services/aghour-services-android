package com.aghourservices.data.model

data class Attachment(
    val id: Int,
    val article_id: Int,
    val resource_url: String,
    val type: String,
    val raw_response: String,
)
