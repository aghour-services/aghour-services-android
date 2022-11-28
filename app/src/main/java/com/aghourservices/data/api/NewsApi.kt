package com.aghourservices.data.api

import com.aghourservices.data.model.Article
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface NewsApi {
    @GET("articles")
    fun loadArticles(): Call<ArrayList<Article>>

    @POST("articles")
    fun createArticle(@Body article: JsonObject, @Header("TOKEN") token: String): Call<Article>
}