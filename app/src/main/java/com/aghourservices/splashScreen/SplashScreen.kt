package com.aghourservices.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.aghourservices.R
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesActivity
import com.aghourservices.user.SignupActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            lateinit var intent: Intent
            val userInfo = UserInfo()

            if (userInfo.isUserLoggedIn(this)) {
                intent = Intent(this, CategoriesActivity::class.java)
            } else {
                intent = Intent(this, SignupActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 1000)
    }
}