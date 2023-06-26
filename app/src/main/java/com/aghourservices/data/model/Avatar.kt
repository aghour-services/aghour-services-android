package com.aghourservices.data.model

data class Avatar(
    val id: Int,
    val user_id: Int,
    val url: String,
    val resource_type: String,
    val raw_response: String,
)
