package com.aghourservices.ui.app.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.aghourservices.R
import com.aghourservices.databinding.ActivitySplashScreenBinding
import com.aghourservices.ui.app.cache.Settings
import com.aghourservices.ui.app.cache.UserInfo
import com.aghourservices.ui.app.user.SignUpActivity
import com.aghourservices.utils.helper.ThemePreference
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkTheme()
        handler()
        firebaseTopic()
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

            val extras = getIntent().extras
            if (extras != null) {
                for (key in extras.keySet()) {
                    intent.putExtra(key.toString(), extras.get(key).toString())
                }
            }

            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 500)
    }

    private fun firebaseTopic() {
        val newsTopic = getString(R.string.news_topic)

        Firebase.messaging.subscribeToTopic(newsTopic)
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