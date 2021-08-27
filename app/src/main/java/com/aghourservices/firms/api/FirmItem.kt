package com.aghourservices.firms.api

data class FirmItem(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val phone_number: String,
)