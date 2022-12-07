package com.aghourservices.data.model

data class Device(
    var device_id: String,
    var token: String,
    var last_usage_time: String? = null,
)
