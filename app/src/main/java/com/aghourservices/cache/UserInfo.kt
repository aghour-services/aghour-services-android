package com.aghourservices.cache

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.aghourservices.user.User

const val PREF_NAME = "user_data"
const val NAME_KEY = "name"
const val MOBILE_KEY = "mobile"
const val TOKEN_KEY = "token"
const val EMAIL_KEY = "email"

class UserInfo {
    fun isUserLoggedIn(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = pref.getString(NAME_KEY, null)
        return name != null
    }

    fun getUserData(context: Context): User {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = pref.getString(NAME_KEY, "Default Name").toString()
        val mobile = pref.getString(MOBILE_KEY, "Default Mobile").toString()
        val email = pref.getString(EMAIL_KEY, "").toString()
        val token = pref.getString(TOKEN_KEY, "").toString()
        return User(name, mobile, email, "", token)
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

    fun clearUserData(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
    }
}