package com.aghourservices.data.api

import com.aghourservices.data.model.Comment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface CommentsApi {
    @POST("articles/{article_id}/comments")
    fun postComment(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
        @Body comment: JsonObject,
    ): Call<Comment>
}