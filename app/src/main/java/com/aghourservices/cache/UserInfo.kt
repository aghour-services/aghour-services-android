package com.aghourservices.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.user.api.User

class UserInfo {
    fun init(context: Context) {
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val userDataPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        var name = userDataPref?.getString("name", null)
        return name != null
    }

    fun getUserData(context: Context): User {
        val userDataPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        var name = userDataPref.getString("name", "").toString()
        var mobile = userDataPref.getString("mobile", "").toString()

        var user = User(name, mobile, "")
        return user
    }

    fun saveUserData(context: Context, user: User) {
        val userDataPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = userDataPref.edit()
        editor.putString("name", user.name)
        editor.putString("mobile", user.mobile)
        editor.apply()
        editor.commit()
    }

    fun clearUserData(context: Context) {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}