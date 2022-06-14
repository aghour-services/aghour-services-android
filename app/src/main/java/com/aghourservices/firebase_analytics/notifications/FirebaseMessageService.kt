package com.aghourservices.firebase_analytics.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.aghourservices.R
import com.aghourservices.constants.Constants.Companion.channelId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("on new token", token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body

        val notificationIntent = Intent(this, DisplayNotificationsActivity::class.java)
        notificationIntent.putExtra("title", title)
        notificationIntent.putExtra("body", body)

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )

        Notification.Builder(this, channelId)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notifications)
            .setLights(NotificationCompat.FLAG_SHOW_LIGHTS, 3000, 1000)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle())
            .setLights(Color.BLUE, 3000, 1000)
            .build()

        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "AGHOUR_CHANNEL_NOTIFICATIONS",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableLights(true)
        channel.lightColor = Color.WHITE
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(0, notification)
    }
}