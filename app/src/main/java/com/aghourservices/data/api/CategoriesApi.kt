package com.aghourservices.data.api

import com.aghourservices.data.model.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface CategoriesApi {
    @GET("categories")
    fun loadCategoriesList(
        @Header("deviceid") deviceId: String
    ): Call<ArrayList<Category>>
}