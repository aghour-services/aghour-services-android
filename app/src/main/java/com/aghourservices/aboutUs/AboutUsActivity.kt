package com.aghourservices.aboutUs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aghourservices.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.aboutAppToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}