package com.aghourservices.user.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInService {
    @POST("sign_in")
    fun signIn(@Body user: JsonObject): Call<User>
}