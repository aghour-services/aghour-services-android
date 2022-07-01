package com.aghourservices.data.api

import com.aghourservices.data.model.Category
import retrofit2.Call
import retrofit2.http.GET

interface CategoriesApi {
    @GET("categories")
    fun loadCategoriesList(): Call<ArrayList<Category>>
}