package com.aghourservices.categories.categoriesApi

import retrofit2.http.GET

interface ApiServices {
    @GET("categories")
    fun loadCategoriesList(): retrofit2.Call<ArrayList<CategoryItem>>
}