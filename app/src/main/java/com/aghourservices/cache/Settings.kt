package com.aghourservices.cache

import android.content.Context
import android.content.SharedPreferences

const val PREFERENCE_NAME: String = "user_settings"
const val SKIP_LOGIN_KEY: String = "skip_login"

class Settings {

    fun showRigsterActivity(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(SKIP_LOGIN_KEY, false)
    }

    fun doNotShowRigsterActivity(context: Context) {
        val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean(SKIP_LOGIN_KEY, true)
        editor.apply()
    }
}