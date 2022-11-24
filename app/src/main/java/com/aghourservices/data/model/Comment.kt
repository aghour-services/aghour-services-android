package com.aghourservices.data.model

import com.google.gson.JsonObject

data class Comment(
    var id: Int,
    var body: String,
    var name: String,
){
    fun toJsonObject(): JsonObject {
        val commentObject = JsonObject()
        val comment = JsonObject()
        comment.addProperty("body", body)
        commentObject.add("comment", comment)
        return commentObject
    }

    fun inValid(): Boolean {
        return body.isEmpty()
    }
}
