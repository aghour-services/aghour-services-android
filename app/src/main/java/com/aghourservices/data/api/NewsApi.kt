package com.aghourservices.data.api

import com.aghourservices.data.model.Article
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface NewsApi {
    @GET("articles")
    fun loadArticles(
        @Header("deviceid") deviceId: String
    ): Call<ArrayList<Article>>

    @POST("articles")
    fun createArticle(
        @Body article: JsonObject,
        @Header("TOKEN") token: String,
        @Header("deviceid") deviceId: String
    ): Call<Article>

    @PUT("articles/{article_id}")
    fun updateArticle(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
        @Body article: JsonObject,
        @Header("deviceid") deviceId: String
    ): Call<Article>

    @DELETE("articles/{article_id}")
    fun deleteArticle(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
        @Header("deviceid") deviceId: String
    ): Call<Article>
}