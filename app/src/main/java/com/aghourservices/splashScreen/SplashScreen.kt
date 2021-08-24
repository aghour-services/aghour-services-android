package com.aghourservices.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.categories.CategoriesActivity

class SplashScreen<splashImage : View?> : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

        val splashScreen = findViewById<splashImage>(R.id.splashImage)as ImageView
        val animation : Animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        splashScreen.startAnimation(animation)
    }
}