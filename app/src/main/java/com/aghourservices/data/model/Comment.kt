package com.aghourservices.data.model

import com.google.gson.JsonObject


data class Comment(
    var id: Int = 0,
    var body: String = "",
    var created_at: String = "",
    var user: User? = null,
    var profile: Profile? = null
) {
    fun toJsonObject(): JsonObject {
        val commentObject = JsonObject()
        val comment = JsonObject()
        comment.addProperty("body", body)
        comment.addProperty("user_name", user?.name)
        commentObject.add("comment", comment)
        return commentObject
    }

    fun inValid(): Boolean {
        return body.isEmpty()
    }
}
