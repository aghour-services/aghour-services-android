package com.aghourservices.data.model

import com.google.gson.JsonObject

data class User(
    var id: Int? = null,
    var name: String,
    var mobile: String,
    var email: String,
    var password: String,
    var token: String = "",
    var role: String = "",
    var verified: Boolean = false,
    var avatar: Avatar? = null,
) {
    fun userObject(): JsonObject {
        val userObject = JsonObject()
        val userDetails = JsonObject()
        userDetails.addProperty("name", name)
        userDetails.addProperty("mobile", mobile)
        userDetails.addProperty("email", email)
        userDetails.addProperty("password", password)
        userObject.add("user", userDetails)
        return userObject
    }
}