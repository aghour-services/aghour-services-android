package com.aghourservices.ui.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aghourservices.R
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.Constants
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import com.aghourservices.utils.services.cache.UserInfo.getProfile
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.aghourservices.utils.services.cache.UserInfo.isUserLoggedIn
import com.google.android.gms.ads.AdView
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.preferences.RatingThreshold

open class BaseActivity : AppCompatActivity() {
    val currentUser by lazy { getUserData(this) }
    val fcmToken by lazy { getFCMToken(this) }
    val isUserLogin by lazy { isUserLoggedIn(this) }
    val currentProfile by lazy { getProfile(this) }
    private lateinit var permissions: Array<String>
    private lateinit var adView: AdView

    fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    fun inAppRating() {
        AppRating.Builder(this)
            .setMinimumLaunchTimes(3)
            .setMinimumDays(3)
            .useGoogleInAppReview()
            .setMinimumLaunchTimesToShowAgain(10)
            .setMinimumDaysToShowAgain(7)
            .setRatingThreshold(RatingThreshold.FOUR)
            .showIfMeetsConditions()
    }

    fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(this, permissions, Constants.REQUEST_CODE)
    }

    fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == (PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }

    fun openGallery() {
        val galleryIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK)
        }
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, Constants.GALLERY_CODE)
    }
}