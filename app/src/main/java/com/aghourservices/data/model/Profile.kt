package com.aghourservices.data.model

data class Profile(
    var id: Int? = null,
    var name: String = "",
    var url: String = "",
    var verified: Boolean = false
)
