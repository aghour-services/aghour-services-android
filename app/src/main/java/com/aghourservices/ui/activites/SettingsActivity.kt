package com.aghourservices.ui.activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance.userApi
import com.aghourservices.databinding.ActivitySettingsBinding
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.Constants
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.helper.Intents
import com.aghourservices.utils.helper.Intents.facebook
import com.aghourservices.utils.helper.Intents.gmail
import com.aghourservices.utils.helper.Intents.loadProfileImage
import com.aghourservices.utils.helper.Intents.rateApp
import com.aghourservices.utils.helper.Intents.shareApp
import com.aghourservices.utils.helper.Intents.showOnCloseDialog
import com.aghourservices.utils.helper.Intents.whatsApp
import com.aghourservices.utils.helper.ThemePreference
import com.aghourservices.utils.services.UserService
import com.aghourservices.utils.services.cache.UserInfo.getProfile
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.aghourservices.utils.services.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.services.cache.UserInfo.saveProfile
import com.google.android.gms.ads.AdView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adView: AdView
    private lateinit var permissions: Array<String>
    private val profile by lazy { getProfile(this) }
    private val user by lazy { getUserData(this) }
    private val isUserLogin by lazy { isUserLoggedIn(this) }
    private var avatarUri: Uri? = null
    private var avatarPart: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUserClick()
        adView()
        checkUser()
        getProfile()
        initPermissions()
    }

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun initUserClick() {
        binding.apply {
            backBtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
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

    private fun profileUserClicks() {
        binding.apply {
            avatarImage.setOnClickListener {
                val intent = Intent(this@SettingsActivity, FullScreenProfileActivity::class.java)
                startActivity(intent)
            }
            addUserImage.setOnClickListener {
                if (!checkStoragePermission()) {
                    requestPermissions()
                } else {
                    openGallery()
                }
            }
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

                        loadProfileImage(this@SettingsActivity, profile.url, binding.avatarImage)

                        binding.userName.apply {
                            text = profile.name
                            if (profile.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tooltipText = context.getString(R.string.verified)
                            } else {
                                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                            }
                            visibility = View.VISIBLE
                        }
                        binding.apply {
                            userEmail.text = profile.email
                            userPhone.text = profile.mobile
                        }
                        profileUserClicks()
                    }
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                stopShimmer()
                binding.apply {
                    userEmail.text = user.email
                    userPhone.text = user.mobile
                }
                binding.userName.apply {
                    text = profile.name
                    if (user.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tooltipText = context.getString(R.string.verified)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                    visibility = View.VISIBLE
                }
                profileUserClicks()
            }
        })
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

    private fun openGallery() {
        val galleryIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK)
        }
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, Constants.GALLERY_CODE)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            avatarUri = data?.data!!
            val file = File(Intents.getRealPathFromURI(this, avatarUri!!)!!)
            Intents.compressFile(this, file)
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            avatarPart =
                MultipartBody.Part.createFormData("user[avatar]", file.name, requestBody)
            binding.avatarImage.setImageURI(avatarUri)
            updateProfileAvatar()
        }
    }

    private fun updateProfileAvatar() {
        val userService = UserService()
        userService.updateAvatar(
            this@SettingsActivity,
            user.token,
            avatarPart,
        )
    }

    private fun initPermissions() {
        permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, Constants.REQUEST_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == (PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }
}