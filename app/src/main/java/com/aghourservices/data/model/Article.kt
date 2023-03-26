package com.aghourservices.data.model

import com.google.gson.JsonObject
import org.json.JSONObject


data class Article(
    var id: Int = 0,
    var description: String = "",
    var created_at: String = "",
    var likes_count: Int = 0,
    var comments_count: Int = 0,
    var liked: Boolean = false,
    var attachments: List<Attachment>? = null,
    var user: User? = null,
    var latest_comment: Comment? = null,
) {
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
        return articleDetails
    }

    fun inValid(): Boolean {
        return description.isEmpty()
    }
}