package com.aghourservices.firebase_analytics

import android.util.Log
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class Event {
    companion object {

        fun sendFirebaseEvent(eventName: String, data: String) {
            var eventName = cleanEventName(eventName)
            Log.d("Event", eventName)
            var firebaseAnalytics = Firebase.analytics
            firebaseAnalytics.logEvent(eventName) {
                param("data", data)
            }
        }

        private fun cleanEventName(eventName: String): String {
            val spaceRegex = """\ +""".toRegex()
            val dotRegex = """\.+""".toRegex()
            val slashRegex = """/+""".toRegex()

            var text = dotRegex.replace(eventName, " ").trim()
            text = spaceRegex.replace(text, "_")
            text = slashRegex.replace(text, "_")

            return text
        }
    }
}