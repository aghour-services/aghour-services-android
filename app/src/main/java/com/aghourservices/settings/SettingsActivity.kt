package com.aghourservices.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.aghourservices.BaseActivity
import com.aghourservices.R
import com.aghourservices.about.AboutActivity
import com.aghourservices.ads.Banner
import com.aghourservices.cache.UserInfo
import com.aghourservices.databinding.ActivitySettingsBinding
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.user.SignUpActivity
import com.google.android.gms.ads.AdView

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUser()
        hideUserLogOut()
        supportActionBar?.hide()

        adView = findViewById(R.id.adView)
        Banner.show(this, adView)

        binding.backBtn.setOnClickListener {
            this.onBackPressed()
        }
        binding.appTheme.setOnClickListener {
            chooseThemeDialog()
        }
        binding.facebook.setOnClickListener {
            facebook()
        }
        binding.email.setOnClickListener {
            gmail()
        }
        binding.whatsApp.setOnClickListener {
            whatsApp(getString(R.string.whats_app_number))
        }
        binding.share.setOnClickListener {
            shareApp()
        }
        binding.rate.setOnClickListener {
            rateApp()
        }
        binding.aboutApp.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
        binding.logOut.setOnClickListener {
            showOnCloseDialog(this)
        }
    }

    private fun checkUser() {
        val userInfo = UserInfo()
        val user = userInfo.getUserData(this)
        if (userInfo.isUserLoggedIn(this)) {
            binding.btnRegister.visibility = View.GONE
            binding.userDataView.visibility = View.VISIBLE
            binding.userName.text = user.name
            binding.userMobile.text = user.mobile
            binding.userEmail.text = user.email
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun hideUserLogOut() {
        val isUserLogin = UserInfo().isUserLoggedIn(this)
        if (isUserLogin) {
            binding.logOut.visibility = View.VISIBLE
        } else {
            binding.logOut.visibility = View.GONE
        }
    }

    private fun chooseThemeDialog() {
        Event.sendFirebaseEvent("App_Theme", "")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_theme_text))
        builder.setNegativeButton(R.string.cancelButton) { _, _ -> }
        val styles = arrayOf(
            getString(R.string.defaultTheme), getString(R.string.light), getString(
                R.string.dark
            )
        )
        val checkedItem = ThemePreference(this).darkMode
        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    ThemePreference(this).darkMode = 0
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    setTheme(R.style.Theme_LightApp)
                    ThemePreference(this).darkMode = 1
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    setTheme(R.style.Theme_DarkApp)
                    ThemePreference(this).darkMode = 2
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }
}