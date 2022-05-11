package com.aghourservices.firms.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ListFirms {
    @GET("firms")
    fun loadFirms(@Query("category_id") category_id: Int): retrofit2.Call<ArrayList<Firm>>
}