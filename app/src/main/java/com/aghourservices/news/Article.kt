package com.aghourservices.news

import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


@RealmClass
open class Article : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var description: String = "".trim()
    var created_at: String = ""

    fun toJsonObject(): JsonObject {
        val firmObject = JsonObject()
        val article = JsonObject()
        article.addProperty("description", description)
        article.addProperty("createdAt", created_at)

        firmObject.add("article", article)
        return firmObject
    }
}