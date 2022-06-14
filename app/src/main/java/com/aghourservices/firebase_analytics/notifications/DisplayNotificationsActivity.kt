package com.aghourservices.firebase_analytics.notifications

import android.os.Bundle
import android.util.Log
import com.aghourservices.BaseActivity
import com.aghourservices.databinding.ActivityDisplayNotificationsBinding

class DisplayNotificationsActivity : BaseActivity() {
    private lateinit var binding: ActivityDisplayNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        supportActionBar?.hide()

        val title = "title"
        val bodyMessage = intent.getStringExtra("newsKey")

        Log.d("TAGS", "$title \n $bodyMessage")

        binding.notifyTitle.text = title
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