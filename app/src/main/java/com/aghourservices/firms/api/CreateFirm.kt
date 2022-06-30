package com.aghourservices.firms.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CreateFirm {
    @POST("firms")
    fun createFirm(@Body firm: JsonObject, @Header("TOKEN") token: String): Call<Firm>
}