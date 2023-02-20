package com.aghourservices.data.api

import com.aghourservices.data.model.Device
import com.aghourservices.data.model.Profile
import com.aghourservices.data.model.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {
    @POST("users/sign_in")
    fun signIn(@Body user: JsonObject): Call<User>

    @POST("users")
    fun signUp(@Body user: JsonObject): Call<User>

    @GET("users/profile")
    fun userProfile(@Header("TOKEN") token: String): Call<Profile>

    @POST("devices")
    fun sendDevice(
        @Body device: Device,
        @Header("fcmToken") fcmToken: String
    ): Call<Device>
}