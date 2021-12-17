package com.aghourservices.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.aghourservices.R
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesActivity
import com.aghourservices.databinding.ActivitySplashScreenBinding
import com.aghourservices.user.SignupActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            Handler(Looper.getMainLooper()).postDelayed({
                lateinit var intent: Intent
                val userInfo = UserInfo()
                intent = if (userInfo.isUserLoggedIn(this)) {
                    Intent(this, CategoriesActivity::class.java)
                } else {
                    Intent(this, SignupActivity::class.java)
                }
                startActivity(intent)
                finish()
            }, 400)
        }, 800)
    }
}