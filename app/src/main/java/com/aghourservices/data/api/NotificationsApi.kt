package com.aghourservices.data.api

import com.aghourservices.data.model.Notification
import retrofit2.Call
import retrofit2.http.*

interface NotificationsApi {
    @GET("notifications")
    fun getNotifications(
        @Header("fcmToken") fcmToken: String,
        @Header("TOKEN") userToken: String
    ): Call<ArrayList<Notification>>
}