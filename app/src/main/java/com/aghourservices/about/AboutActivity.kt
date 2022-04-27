package com.aghourservices.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.databinding.ActivityAboutBinding
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