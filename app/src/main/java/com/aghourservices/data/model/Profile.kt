package com.aghourservices.data.model

data class Profile(
    var id: Int? = null,
    var name: String = "",
    var email: String = "",
    var mobile: String = "",
    var url: String = "",
    var verified: Boolean = false
)
