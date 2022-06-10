package com.aghourservices.firebase_analytics.notifications

import android.content.Intent
import android.os.Bundle
import com.aghourservices.BaseActivity
import com.aghourservices.databinding.ActivityDisplayNotificationsBinding

class DisplayNotificationsActivity : BaseActivity() {
    private lateinit var binding: ActivityDisplayNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNotification()
    }

    private fun initNotification() {
        val intent: Intent = intent
        val title = intent.getStringExtra("title")
        val bodyMessage = intent.getStringExtra("bodyMessage")

        binding.title.text = title
        binding.bodyMessage.text = bodyMessage

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}