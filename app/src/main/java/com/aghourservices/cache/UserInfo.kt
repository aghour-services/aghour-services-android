package com.aghourservices.cache

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.aghourservices.user.api.User

class UserInfo {
    fun isUserLoggedIn(context: Context): Boolean {
        val userDataPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val name = userDataPref?.getString("name", null)
        return name != null
    }

    fun getUserData(context: Context): User {
        val userDataPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val name = userDataPref.getString("name", "").toString()
        val mobile = userDataPref.getString("mobile", "").toString()

        return User(name, mobile, "")
    }

    fun saveUserData(context: Context, user: User) {
        val userDataPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = userDataPref.edit()
        editor.putString("name", user.name)
        editor.putString("mobile", user.mobile)
        editor.apply()
    }

    fun clearUserData(context: Context) {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}