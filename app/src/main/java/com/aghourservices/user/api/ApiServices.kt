package com.aghourservices.user.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {
    @POST("users")
    fun createUser(@Body user: JsonObject): Call<User>
}