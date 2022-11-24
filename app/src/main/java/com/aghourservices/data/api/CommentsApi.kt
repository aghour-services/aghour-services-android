package com.aghourservices.data.api

import com.aghourservices.data.model.Comment
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentsApi {
    @POST("articles/{article_id}/comments")
    fun postComment(@Path("article_id") articleId: String): Call<Comment>
}