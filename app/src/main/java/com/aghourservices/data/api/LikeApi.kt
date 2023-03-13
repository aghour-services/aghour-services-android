package com.aghourservices.data.api

import com.aghourservices.data.model.Article
import com.aghourservices.data.model.User
import retrofit2.Call
import retrofit2.http.*

interface LikeApi {
    @GET("articles/{article_id}/likes")
    fun getLikes(
        @Path("article_id") articleId: Int,
    ): Call<ArrayList<User>>

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