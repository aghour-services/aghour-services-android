package com.aghourservices.news.api

import com.aghourservices.news.Article
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesAPI {

    @GET("articles")
    fun loadArticles(@Query("category_id") category_id: Int): retrofit2.Call<ArrayList<Article>>
}