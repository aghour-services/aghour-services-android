package com.aghourservices.data.api

import com.aghourservices.data.model.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("users/sign_in")
    fun signIn(@Body user: JsonObject): Call<User>

    @POST("users")
    fun signUp(@Body user: JsonObject): Call<User>
}