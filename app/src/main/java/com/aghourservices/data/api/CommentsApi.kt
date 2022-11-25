package com.aghourservices.data.api

import com.aghourservices.data.model.Category
import com.aghourservices.data.model.Comment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface CommentsApi {

    @GET("articles/{article_id}/comments")
    fun loadComments(
        @Path("article_id") articleId: Int,
    ): Call<ArrayList<Comment>>


    @POST("articles/{article_id}/comments")
    fun postComment(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
        @Body comment: JsonObject,
    ): Call<Comment>
}