package com.aghourservices.utils.helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance.newsApi
import com.aghourservices.ui.main.notification.Notification.notificationManager
import com.aghourservices.ui.main.notification.Notification.sendNotification
import com.aghourservices.utils.helper.Constants.Companion.NOTIFICATION_ID
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
        val descriptionBody = description.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
        val retrofitBuilder = newsApi.createArticle(
            userToken,
            fcmToken,
            descriptionBody,
            imagePart
        )
        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    if (isVerified == true){
                        val updatedNotification =
                            sendNotification(context, "ماشي يعم الأدمن 😃❤", "تم إنشاء الخبر بنجاح")
                        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
                    }else{
                        val updatedNotification =
                            sendNotification(context, "تم إرسال الخبر", "هنراجع البيانات وهنضيفه في أقرب وقت")
                        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
                    }
                }else{
                    val updatedNotification =
                        sendNotification(context, getString(R.string.create_article_failed), getString(R.string.try_again))
                    notificationManager.notify(NOTIFICATION_ID, updatedNotification)
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                val updatedNotification =
                    sendNotification(context, getString(R.string.create_article_failed), getString(R.string.try_again))
                notificationManager.notify(NOTIFICATION_ID, updatedNotification)
            }
        })

        ContextCompat.startForegroundService(context, startIntent)
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