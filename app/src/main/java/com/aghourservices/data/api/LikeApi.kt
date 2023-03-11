package com.aghourservices.data.api

import com.aghourservices.data.model.Article
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface LikeApi {
    @POST("articles/{article_id}/likes")
    fun likeArticle(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
    ): Call<Article>

    @DELETE("articles/{article_id}/likes/unlike")
    fun unLikeArticle(
        @Path("article_id") articleId: Int,
        @Header("TOKEN") token: String,
    ): Call<Article>
}