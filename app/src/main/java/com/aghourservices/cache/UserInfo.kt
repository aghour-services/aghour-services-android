package com.aghourservices.cache

import android.content.Context
import android.content.SharedPreferences
import com.aghourservices.user.User

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
        val email = userDataPref.getString("email", "").toString()
        val token = userDataPref.getString("token", "").toString()
        return User(name, mobile, email, "", token)
    }

    fun saveUserData(context: Context, user: User) {
        val userDataPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = userDataPref.edit()
        editor.putString("name", user.name)
        editor.putString("mobile", user.mobile)
        editor.putString("email", user.email)
        editor.putString("token", user.token)
        editor.apply()
    }

    fun clearUserData(context: Context) {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}