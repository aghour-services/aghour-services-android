package com.aghourservices.data.api

import com.aghourservices.data.model.Comment
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface CommentsApi {

    @GET("articles/{article_id}/comments")
    fun loadComments(
        @Path("article_id") articleId: Int,
        @Header("deviceid") deviceId: String
    ): Call<ArrayList<Comment>>


    @POST("articles/{article_id}/comments")
    fun postComment(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
        @Body comment: JsonObject,
        @Header("deviceid") deviceId: String
    ): Call<Comment>

    @PUT("articles/{article_id}/comments/{comment_id}")
    fun updateComment(
        @Path("article_id") articleId: Int,
        @Path("comment_id") commentId: Int,
        @Header("TOKEN") token: String,
        @Body comment: JsonObject,
        @Header("deviceid") deviceId: String
    ): Call<Comment>

    @DELETE("articles/{article_id}/comments/{comment_id}")
    fun deleteComment(
        @Path("article_id") articleId: Int,
        @Path("comment_id") commentId: Int,
        @Header("TOKEN") token: String,
        @Header("deviceid") deviceId: String
    ): Call<Comment>
}