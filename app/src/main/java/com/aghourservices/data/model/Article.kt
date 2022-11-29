package com.aghourservices.data.model

import com.google.gson.JsonObject
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.json.JSONObject


@RealmClass
open class Article : RealmObject() {
    @PrimaryKey
    var id: Int = 0

    @Index
    var description: String = ""
    var created_at: String = ""
    var name: String? = null
    var isFavorite: Boolean = false

    fun toJsonObject(): JsonObject {
        val articleObject = JsonObject()
        val article = JsonObject()
        article.addProperty("description", description)
        articleObject.add("article", article)
        return articleObject
    }

    // Android Sdk JSONObject
    fun toJSONObject(): JSONObject {
        val articleDetails = JSONObject()
        articleDetails.put("id", id)
        articleDetails.put("description", description)
        articleDetails.put("created_at", created_at)
        articleDetails.put("isFavorite", isFavorite)
        return articleDetails
    }

    fun inValid(): Boolean {
        return description.isEmpty()
    }
}