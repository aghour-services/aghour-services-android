package com.aghourservices.data.api

import com.aghourservices.data.model.Search
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApi {
    @GET("search")
    fun search(
        @Query("keyword") searchKeyword: String,
        @Header("deviceid") deviceId: String
    ): retrofit2.Call<ArrayList<Search>>
}