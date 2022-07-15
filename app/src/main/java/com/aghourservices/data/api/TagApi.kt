package com.aghourservices.data.api

import com.aghourservices.data.model.Firm
import com.aghourservices.data.model.Tag
import com.aghourservices.data.model.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TagApi {
    @GET("categories/{category_id}/tags")
    fun loadTags(@Path("category_id") id: Int): Call<ArrayList<Tag>>
}