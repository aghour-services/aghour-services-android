package com.aghourservices.aboutUs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.databinding.ActivityAboutUsBinding
import com.google.android.gms.ads.AdView

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.aboutAppToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        adView = findViewById(R.id.adView)
        AghourAdManager.displayBannerAd(this, adView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}