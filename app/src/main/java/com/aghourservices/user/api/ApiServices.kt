package com.aghourservices.user.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {
    @POST("users")
    fun createUser(@Body userMode: User): Call<User>
}