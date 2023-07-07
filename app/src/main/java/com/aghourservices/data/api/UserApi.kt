package com.aghourservices.data.api

import com.aghourservices.data.model.Device
import com.aghourservices.data.model.Profile
import com.aghourservices.data.model.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApi {
    @POST("users/sign_in")
    fun signIn(@Body user: JsonObject): Call<User>

    @Multipart
    @POST("users")
    fun signUp(
        @Part("user[name]") name: RequestBody,
        @Part("user[email]") email: RequestBody,
        @Part("user[mobile]") mobile: RequestBody,
        @Part("user[password]") password: RequestBody,
        @Part avatar: MultipartBody.Part?,
    ): Call<User>

    @Multipart
    @PUT("users")
    fun update(
        @Header("TOKEN") token: String,
        @Part("user[name]") name: RequestBody? = null,
        @Part("user[email]") email: RequestBody? = null,
        @Part("user[mobile]") mobile: RequestBody? = null,
        @Part("user[password]") password: RequestBody? = null,
        @Part avatar: MultipartBody.Part?,
    ): Call<User>

    @GET("users/profile")
    fun userProfile(@Header("TOKEN") token: String): Call<Profile>

    @POST("devices")
    fun sendDevice(
        @Body device: Device,
        @Header("fcmToken") fcmToken: String
    ): Call<Device>
}