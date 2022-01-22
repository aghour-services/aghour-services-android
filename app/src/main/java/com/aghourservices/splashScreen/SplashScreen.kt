package com.aghourservices.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.MainActivity
import com.aghourservices.R
import com.aghourservices.ads.Interstitial
import com.aghourservices.cache.Settings
import com.aghourservices.cache.UserInfo
import com.aghourservices.databinding.ActivitySplashScreenBinding
import com.aghourservices.user.SignUpActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.messaging.subscribeToTopic("News")
            .addOnCompleteListener { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
                Log.d("FCM", msg)
            }
        Handler(Looper.getMainLooper()).postDelayed({
            lateinit var intent: Intent
            val userInfo = UserInfo()
            val settings = Settings()
            val skip = userInfo.isUserLoggedIn(this) || settings.showRigsterActivity(this)

            intent = if (skip) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, SignUpActivity::class.java)
            }
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }, 500)
    }
}