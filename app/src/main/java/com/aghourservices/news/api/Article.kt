package com.aghourservices.news.api

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
        val articleObject = JsonObject()
        val article = JsonObject()
        article.addProperty("description", description)

        articleObject.add("article", article)
        return articleObject
    }
}