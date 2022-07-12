package com.aghourservices.data.api

import com.aghourservices.data.model.Firm
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface FirmsApi {
    @POST("firms")
    fun createFirm(@Body firm: JsonObject, @Header("TOKEN") token: String): Call<Firm>

    @GET("firms")
    fun loadFirms(
        @Query("category_id") category_id: Int,
        @Query("tags") tags: ArrayList<String>
    ): Call<ArrayList<Firm>>
}