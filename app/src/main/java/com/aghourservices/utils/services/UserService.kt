package com.aghourservices.utils.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.aghourservices.data.model.User
import com.aghourservices.data.network.RetrofitInstance
import com.aghourservices.utils.helper.Constants
import com.aghourservices.utils.services.notifications.Notification
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserService : Service() {

    fun updateAvatar(
        context: Context,
        token: String,
        avatarPart: MultipartBody.Part?
    ) {
        val retrofitInstance =
            RetrofitInstance.userApi.update(token = token, avatar = avatarPart)

        val startIntent = Intent(context, UserService::class.java)
        retrofitInstance.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val updatedNotification =
                        Notification.sendNotification(
                            context.applicationContext,
                            "تحديث الحساب",
                            "تم تحديث الصورة الشخصية بنجاح ✅"
                        )
                    Notification.notificationManager.notify(
                        Constants.NOTIFICATION_ID,
                        updatedNotification
                    )
                } else {
                    val updatedNotification =
                        Notification.sendNotification(
                            context.applicationContext,
                            "فشل تحديث الصورة الشخصية ❌",
                            "حاول مرة أخرى."
                        )
                    Notification.notificationManager.notify(
                        Constants.NOTIFICATION_ID,
                        updatedNotification
                    )
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                val updatedNotification =
                    Notification.sendNotification(
                        context.applicationContext,
                        "فشل تحديث الصورة الشخصية ❌",
                        "لا يوجد إنترنت",
                        )
                Notification.notificationManager.notify(
                    Constants.NOTIFICATION_ID,
                    updatedNotification
                )
            }
        })

        ContextCompat.startForegroundService(context.applicationContext, startIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            Constants.NOTIFICATION_ID,
            Notification.sendNotification(this, "إنتظر من فضلك...", "جار تحديث الصورة الشخصية...")
        )
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}