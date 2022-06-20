package com.aghourservices.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.aghourservices.BaseActivity
import com.aghourservices.MainActivity
import com.aghourservices.R
import com.aghourservices.cache.Settings
import com.aghourservices.cache.UserInfo
import com.aghourservices.databinding.ActivitySplashScreenBinding
import com.aghourservices.settings.ThemePreference
import com.aghourservices.user.SignUpActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

@SuppressLint("CustomSplashScreen")
class SplashScreen : BaseActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        checkTheme()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseTopic()


        val extras = intent?.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                Log.d("Splash", "$key -> ${extras.get(key)}")
            }
        }
    }

    private fun handler() {
        Handler(Looper.getMainLooper()).postDelayed({
            lateinit var intent: Intent
            val settings = Settings()
            val user = UserInfo()

            val skip = user.isUserLoggedIn(this) || settings.showRigsterActivity(this)

            intent = if (skip) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, SignUpActivity::class.java)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 500)
    }

    private fun firebaseTopic() {
        Firebase.messaging.subscribeToTopic("News")
            .addOnCompleteListener { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
                Log.d("FCM", msg)
            }
    }

    private fun checkTheme() {
        when (ThemePreference(this).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}