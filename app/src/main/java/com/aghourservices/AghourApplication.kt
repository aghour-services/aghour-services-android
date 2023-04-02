package com.aghourservices

import android.app.Application
import com.aghourservices.ui.main.notification.Notification
import io.realm.Realm
import io.realm.RealmConfiguration

class AghourApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Notification.createNotificationChannel(this)

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
}