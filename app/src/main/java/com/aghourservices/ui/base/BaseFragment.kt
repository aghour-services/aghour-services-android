package com.aghourservices.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aghourservices.R
import com.aghourservices.utils.helper.Constants
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.helper.HasBottomNavigation
import com.aghourservices.utils.helper.HasToolbar
import com.aghourservices.utils.services.cache.UserInfo
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment(), HasToolbar, HasBottomNavigation {
    private lateinit var bottomNavigation: ConstraintLayout
    private lateinit var dashboardToolbar: MaterialToolbar
    private lateinit var permissions: Array<String>

    val currentUser by lazy { getUserData(requireContext()) }
    val fcmToken by lazy { getFCMToken(requireContext()) }
    val isUserLogin by lazy { UserInfo.isUserLoggedIn(requireContext()) }
    val currentProfile by lazy { UserInfo.getProfile(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
        bottomNavigation = requireActivity().findViewById(R.id.bottomViewContainer)
        dashboardToolbar = requireActivity().findViewById(R.id.toolbar)
    }

    fun onSNACK(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.BLACK)
        val textView =
            snackBarView.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 18f
        snackBar.show()
    }

    override fun showBottomNavigation() {
        bottomNavigation.visibility = View.VISIBLE
    }

    override fun hideBottomNavigation() {
        bottomNavigation.visibility = View.GONE
    }

    override fun showToolbar() {
        dashboardToolbar.visibility = View.VISIBLE
    }

    override fun hideToolbar() {
        dashboardToolbar.visibility = View.GONE
    }

    @SuppressLint("InlinedApi")
    fun initStoragePermissions() {
        permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
    }

    fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(requireActivity(), permissions, Constants.REQUEST_CODE)
    }

    fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                requireContext(),
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