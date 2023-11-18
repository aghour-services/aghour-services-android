package com.aghourservices.utils.services.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aghourservices.R
import com.aghourservices.ui.activities.DashboardActivity
import com.aghourservices.utils.helper.Constants.Companion.FCM_CHANNEL
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("on new token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        val title = data["title"]
        val body = data["body"]
        val articleId = data["article_id"]
        val articleImage = data["article_image"]
        val userAvatar = data["user_avatar"]
        val userId = data["user_id"]
        val commentId = data["comment_id"]

        val notificationIntent = Intent(this, DashboardActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val bundle = Bundle().apply {
            putString("title", title)
            putString("body", body)
            putString("article_id", articleId)
            putString("article_image", articleImage)
            putString("user_avatar", userAvatar)
            putString("user_id", userId)
            putString("comment_id", commentId)
        }
        notificationIntent.putExtras(bundle)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, FCM_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.aghour)
            .setLights(NotificationCompat.FLAG_SHOW_LIGHTS, 1000, 3000)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setLargeIcon(loadImageAsBitmap(this, userAvatar.toString()))
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(loadImageAsBitmap(this, articleImage.toString()))
            )
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.splashScreenBg))
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(FCM_CHANNEL, 0, notification)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                FCM_CHANNEL, FCM_CHANNEL,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setShowBadge(true)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun loadImageAsBitmap(context: Context, url: String): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}