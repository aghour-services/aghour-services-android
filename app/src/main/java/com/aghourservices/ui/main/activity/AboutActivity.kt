package com.aghourservices.ui.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.databinding.ActivityAboutBinding
import com.aghourservices.utils.ads.Banner
import com.google.android.gms.ads.AdView

class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adView = findViewById(R.id.adView)
        Banner.show(this, adView)

        binding.backBtn.setOnClickListener {
            this.onBackPressed()
        }
    }
}