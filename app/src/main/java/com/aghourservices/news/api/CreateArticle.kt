package com.aghourservices.news.api

import com.aghourservices.firms.Firm
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CreateArticle {
    @POST("articles")
    fun createFirm(@Body article: JsonObject, @Header("TOKEN") token: String): retrofit2.Call<Firm>
}
