package com.aghourservices.data.model

import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


data class Comment(
    var id: Int = 0,
    var body: String = "",
    var user: User? = null
) {
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
