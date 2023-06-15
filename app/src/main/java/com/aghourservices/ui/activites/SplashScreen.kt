package com.aghourservices.ui.activites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.databinding.ActivitySplashScreenBinding
import com.aghourservices.utils.helper.Intents.checkTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        checkTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseTopic()
        handler()
    }

    private fun handler() {
        handler.postDelayed({
            val intent = Intent(this, DashboardActivity::class.java)

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
}