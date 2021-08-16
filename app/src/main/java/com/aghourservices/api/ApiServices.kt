package com.aghourservices.api

import retrofit2.http.GET

interface ApiServices {
    @GET("photos")
    fun loadCategoriesList(): retrofit2.Call<ArrayList<CategoryItem>>
}