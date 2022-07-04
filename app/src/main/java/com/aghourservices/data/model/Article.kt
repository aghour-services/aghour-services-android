package com.aghourservices.data.model

import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


@RealmClass
open class Article : RealmObject() {
    @PrimaryKey
    var id: Int = 0

    @Index
    var description: String = ""
    var created_at: String = ""

    fun toJsonObject(): JsonObject {
        val articleObject = JsonObject()
        val article = JsonObject()
        article.addProperty("description", description)
        articleObject.add("article", article)
        return articleObject
    }

    fun inValid(): Boolean {
        return description.isEmpty()
    }
}