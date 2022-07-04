package com.aghourservices.ui.main.cache

import android.content.Context
import android.content.SharedPreferences
import com.aghourservices.utils.helper.Constants.Companion.PREFERENCE_NAME
import com.aghourservices.utils.helper.Constants.Companion.SKIP_LOGIN_KEY

object Settings {

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