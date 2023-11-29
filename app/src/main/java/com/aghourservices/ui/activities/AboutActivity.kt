package com.aghourservices.ui.activities

import android.os.Bundle
import com.aghourservices.R
import com.aghourservices.databinding.ActivityAboutBinding
import com.aghourservices.ui.base.BaseActivity
import com.aghourservices.utils.ads.Banner
import com.google.android.gms.ads.AdView

class AboutActivity : BaseActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adView = findViewById(R.id.adView)
        Banner.show(this, adView)

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}