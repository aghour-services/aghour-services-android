package com.aghourservices.search.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("search")
    fun search(@Query("keyword") searchKeyword: String): retrofit2.Call<ArrayList<SearchResult>>
}