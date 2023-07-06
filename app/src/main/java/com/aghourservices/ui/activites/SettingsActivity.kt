package com.aghourservices.ui.activites

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance.userApi
import com.aghourservices.databinding.ActivitySettingsBinding
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.helper.Intents.facebook
import com.aghourservices.utils.helper.Intents.gmail
import com.aghourservices.utils.helper.Intents.rateApp
import com.aghourservices.utils.helper.Intents.shareApp
import com.aghourservices.utils.helper.Intents.showOnCloseDialog
import com.aghourservices.utils.helper.Intents.whatsApp
import com.aghourservices.utils.helper.ThemePreference
import com.aghourservices.utils.services.cache.UserInfo.getProfile
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.aghourservices.utils.services.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.services.cache.UserInfo.saveProfile
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var adView: AdView
    private val user by lazy { getUserData(this) }
    private val profile by lazy { getProfile(this) }
    private val isUserLogin by lazy { isUserLoggedIn(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adView()
        initUserClick()
    }

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun initUserClick() {
        binding.apply {
            backBtn.setOnClickListener {
                this@SettingsActivity.onBackPressed()
            }
            appTheme.setOnClickListener {
                chooseThemeDialog()
            }
            facebook.setOnClickListener {
                facebook(this@SettingsActivity)
            }
            email.setOnClickListener {
                gmail(this@SettingsActivity)
            }
            whatsApp.setOnClickListener {
                whatsApp(this@SettingsActivity, getString(R.string.whats_app_number))
            }
            share.setOnClickListener {
                shareApp(this@SettingsActivity)
            }
            rate.setOnClickListener {
                rateApp(this@SettingsActivity)
            }
            aboutApp.setOnClickListener {
                val intent = Intent(this@SettingsActivity, AboutActivity::class.java)
                startActivity(intent)
            }
            logOut.setOnClickListener {
                showOnCloseDialog(this@SettingsActivity)
            }
        }
    }

    private fun checkUser() {
        if (isUserLogin) {
            binding.apply {
                userLayout.visibility = View.VISIBLE
                logOut.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                stopShimmer()
                createAccountLayout.visibility = View.VISIBLE
                userLayout.visibility = View.INVISIBLE
                logOut.visibility = View.GONE
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finishAffinity()
        }
    }

    private fun getProfile() {
        val retrofitInstance = userApi.userProfile(user.token)

        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    stopShimmer()
                    val profile = response.body()
                    if (profile != null) {
                        saveProfile(
                            this@SettingsActivity,
                            profile.id!!,
                            profile.name,
                            profile.verified
                        )
                        Glide.with(this@SettingsActivity)
                            .load(profile.url)
                            .placeholder(R.mipmap.user)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.avatarImage)
                        binding.userName.apply {
                            text = profile.name
                            if (profile.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tooltipText = context.getString(R.string.verified)
                            } else {
                                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                            }
                        }
                        binding.userPhone.text = user.mobile
                        binding.userEmail.text = user.email
                    }
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                stopShimmer()
                binding.apply {
                    userPhone.text = user.mobile
                    userEmail.text = user.email
                }
                binding.userName.apply {
                    text = profile.name
                    if (profile.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tooltipText = context.getString(R.string.verified)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                }
            }
        })
    }

    override fun onStart() {
        checkUser()
        getProfile()
        super.onStart()
    }

    private fun chooseThemeDialog() {
        Event.sendFirebaseEvent("App_Theme", "")
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
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
                    ThemePreference(this).darkMode = 1
                    dialog.dismiss()
                }

                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    ThemePreference(this).darkMode = 2
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    private fun stopShimmer() {
        binding.apply {
            profileShimmer.stopShimmer()
            profileShimmer.visibility = View.INVISIBLE
        }
    }
}