package com.aghourservices.user.api

import com.google.gson.JsonObject

data class User(
    var name: String,
    var mobile: String,
    var password: String
) {
    fun userObject(): JsonObject {
        val userObject = JsonObject()
        val userDetails = JsonObject()
        userDetails.addProperty("name", name)
        userDetails.addProperty("mobile", mobile)
        userDetails.addProperty("password", password)
        userObject.add("user", userDetails)
        return userObject
    }
}