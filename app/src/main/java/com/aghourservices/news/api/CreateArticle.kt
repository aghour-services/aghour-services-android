package com.aghourservices.news.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CreateArticle {
    @POST("articles")
    fun createArticle(@Body article: JsonObject, @Header("TOKEN") token: String): Call<Article>
}
