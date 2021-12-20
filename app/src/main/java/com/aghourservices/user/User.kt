package com.aghourservices.user

import com.google.gson.JsonObject

data class User(
    var name: String,
    var mobile: String,
    var email: String,
    var password: String,
    var token: String
) {
    fun userObject(): JsonObject {
        val userObject = JsonObject()
        val userDetails = JsonObject()
        userDetails.addProperty("name", name)
        userDetails.addProperty("mobile", mobile)
        userDetails.addProperty("email", email)
        userDetails.addProperty("password", password)
        userDetails.addProperty("token", token)
        userObject.add("user", userDetails)
        return userObject
    }
}