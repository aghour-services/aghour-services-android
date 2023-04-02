package com.aghourservices.data.api

import com.aghourservices.data.model.Article
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NewsApi {
    @GET("articles")
    fun loadArticles(
        @Header("TOKEN") userToken: String,
        @Header("fcmToken") fcmToken: String
    ): Call<ArrayList<Article>>

    @Multipart
    @POST("articles")
    fun createArticle(
        @Header("TOKEN") token: String,
        @Header("fcmToken") fcmToken: String,
        @Part("article[description]") description: RequestBody,
        @Part attachment: MultipartBody.Part?,
    ): Call<Article>

    @PUT("articles/{article_id}")
    fun updateArticle(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
        @Body article: JsonObject,
        @Header("fcmToken") fcmToken: String
    ): Call<Article>

    @DELETE("articles/{article_id}")
    fun deleteArticle(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
        @Header("fcmToken") fcmToken: String
    ): Call<Article>
}