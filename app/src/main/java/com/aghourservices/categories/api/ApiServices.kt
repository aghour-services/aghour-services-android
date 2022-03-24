package com.aghourservices.categories.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiServices {
    @GET("categories")
    fun loadCategoriesList(): Call<ArrayList<Category>>
}