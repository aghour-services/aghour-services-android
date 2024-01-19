package com.aghourservices.ui.base

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
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

    fun inAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= 3
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    0
                )
            }
        }
    }

    @SuppressLint("InlinedApi")
    fun initStoragePermissions() {
        permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
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

//    protected fun fullScreenAvatar(imageUrl: String?, view: View?) {
//        StfalconImageViewer.Builder(
//            this,
//            arrayListOf(imageUrl)
//        ) { imageView, image ->
//            Glide.with(this)
//                .load(image)
//                .placeholder(R.color.image_bg)
//                .error(R.drawable.image_placeholder)
//                .into(imageView)
//        }
//            .withHiddenStatusBar(false)
//            .allowSwipeToDismiss(true)
//            .allowZooming(true)
//            .withBackgroundColor(Color.BLACK)
//            .show()
//    }
//
//    protected fun fullScreenArticleAttachments(imageUrl: String?, view: View?) {
//        StfalconImageViewer.Builder(
//            this,
//            arrayListOf(imageUrl)
//        ) { imageView, image ->
//            Glide.with(this)
//                .load(image)
//                .placeholder(R.color.image_bg)
//                .error(R.drawable.image_placeholder)
//                .into(imageView)
//        }
//            .withHiddenStatusBar(false)
//            .allowSwipeToDismiss(true)
//            .allowZooming(true)
//            .withBackgroundColor(Color.BLACK)
//            .show(true)
//    }
}