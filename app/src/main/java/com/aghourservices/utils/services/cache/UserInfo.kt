package com.aghourservices.utils.services.cache

import android.content.Context
import android.content.SharedPreferences
import com.aghourservices.data.model.Profile
import com.aghourservices.data.model.User
import com.aghourservices.utils.helper.Constants.Companion.EMAIL_KEY
import com.aghourservices.utils.helper.Constants.Companion.MOBILE_KEY
import com.aghourservices.utils.helper.Constants.Companion.NAME_KEY
import com.aghourservices.utils.helper.Constants.Companion.PREF_NAME
import com.aghourservices.utils.helper.Constants.Companion.TOKEN_KEY


object UserInfo {
    fun isUserLoggedIn(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val token = pref.getString(TOKEN_KEY, null)
        return token != null
    }

    fun getUserData(context: Context): User {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = pref.getString(NAME_KEY, "Default Name").toString()
        val mobile = pref.getString(MOBILE_KEY, "Default Mobile").toString()
        val email = pref.getString(EMAIL_KEY, "").toString()
        val token = pref.getString(TOKEN_KEY, "").toString()
        return User(null, name, mobile, email, "", token)
    }

    fun saveUserData(context: Context, user: User) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(NAME_KEY, user.name)
        editor.putString(MOBILE_KEY, user.mobile)
        editor.putString(EMAIL_KEY, user.email)
        editor.putString(TOKEN_KEY, user.token)
        editor.apply()
    }

    fun getProfile(context: Context): Profile {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = pref.getInt("id", 0)
        val name = pref.getString("profile_name", "").toString()
        val isVerified = pref.getBoolean("is_verified", false)
        return Profile(id, name, "", "", "", isVerified)
    }

    fun saveProfile(context: Context, id: Int, name: String, isVerified: Boolean) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putInt("id", id)
        editor.putString("profile_name", name)
        editor.putBoolean("is_verified", isVerified)
        editor.apply()
    }

    fun saveFCMToken(context: Context, fcmToken: String) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString("fcmToken", fcmToken)
        editor.apply()
    }

    fun getFCMToken(context: Context): String {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString("fcmToken", "").toString()
    }

    fun clearUserData(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
    }
}