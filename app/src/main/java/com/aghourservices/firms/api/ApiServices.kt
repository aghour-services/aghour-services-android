package com.aghourservices.firms.api

import com.aghourservices.firms.api.Firm
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("firms")
    fun loadFirms(@Query("category_id") category_id: Int): retrofit2.Call<ArrayList<Firm>>
}