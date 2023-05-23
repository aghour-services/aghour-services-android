package com.aghourservices.utils.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.aghourservices.data.model.Article
import com.aghourservices.data.network.RetrofitInstance.articlesApi
import com.aghourservices.utils.helper.Constants.Companion.NOTIFICATION_ID
import com.aghourservices.utils.services.notifications.Notification.notificationManager
import com.aghourservices.utils.services.notifications.Notification.sendNotification
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateArticleService : Service() {

    fun publishArticle(
        context: Context,
        userToken: String,
        fcmToken: String,
        description: String,
        imagePart: MultipartBody.Part?,
        isVerified: Boolean? = null
    ) {
        val startIntent = Intent(context, CreateArticleService::class.java)
        val descriptionBody =
            description.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
        val retrofitBuilder = articlesApi.createArticle(
            userToken,
            fcmToken,
            descriptionBody,
            imagePart
        )
        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                Log.d("RESPONSE_CODE", "onResponse: ${response.code()}")
                if (response.code() == 201) {
                    if (isVerified == true) {
                        val updatedNotification =
                            sendNotification(
                                context.applicationContext,
                                "ماشي يعم الأدمن 😃❤",
                                "تم إنشاء الخبر بنجاح"
                            )
                        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
                    } else {
                        val updatedNotification =
                            sendNotification(
                                context.applicationContext,
                                "تم إرسال الخبر",
                                "هنراجع البيانات وهنضيفه في أقرب وقت"
                            )
                        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
                    }
                } else {
                    val updatedNotification =
                        sendNotification(
                            context.applicationContext,
                            "فشل إنشاء الخبر",
                            "حاول مرة تانية."
                        )
                    notificationManager.notify(NOTIFICATION_ID, updatedNotification)
                    Log.d("ARTICLE_FAIL", "onResponse: ${response.code()} \n ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                val updatedNotification =
                    sendNotification(
                        context.applicationContext,
                        "فشل إنشاء الخبر",
                        "لا يوجد إنترنت."
                    )
                notificationManager.notify(NOTIFICATION_ID, updatedNotification)
            }
        })

        ContextCompat.startForegroundService(context.applicationContext, startIntent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            sendNotification(this, "إنتظر من فضلك..", "جاري إنشاء الخبر...")
        )
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}