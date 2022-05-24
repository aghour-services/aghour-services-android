package com.aghourservices.firebase_analytics

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aghourservices.MainActivity
import com.aghourservices.R
import com.aghourservices.constants.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("on new token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        showNotification(remoteMessage)

        //Save Notification if app is already open
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("message", remoteMessage.notification!!.body!!)
        startActivity(intent)
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val builder = NotificationCompat.Builder(this, Constants.channelId)
            .setSmallIcon(R.drawable.ic_launcher_round)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
        if (remoteMessage.notification?.imageUrl == null) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            ).priority = NotificationCompat.PRIORITY_DEFAULT
        } else {
            val bitmap: Bitmap = loadImageToBitMap(remoteMessage)
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap)).priority =
                NotificationCompat.PRIORITY_DEFAULT
        }
        val notManegers = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notManegers.notify(10, builder.build())

    }

    @SuppressLint("CheckResult")
    private fun loadImageToBitMap(remoteMessage: RemoteMessage): Bitmap {
        var bitmap: Bitmap? = null
        Glide.with(this).asBitmap().load(remoteMessage.notification?.imageUrl)
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    bitmap = resource!!
                    return true
                }
            })
        return bitmap!!
    }
}
