package com.aghourservices

import android.app.Application
import com.aghourservices.utils.services.notifications.FCMNotificationService
import com.aghourservices.utils.services.notifications.Notification
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.RealmConfiguration

class AghourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Notification.createNotificationChannel(this)
        FCMNotificationService().createNotificationChannel(this)

        Realm.init(this)
        val configuration = RealmConfiguration.Builder()
            .name("offline.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(configuration)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        clearGlideMemory()
    }

    private fun clearGlideMemory() {
        Glide.get(this).clearMemory()
    }
}