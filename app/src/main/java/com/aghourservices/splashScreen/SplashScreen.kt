package com.aghourservices.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.aghourservices.BaseActivity
import com.aghourservices.R
import com.aghourservices.categories.CategoriesActivity

class SplashScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}