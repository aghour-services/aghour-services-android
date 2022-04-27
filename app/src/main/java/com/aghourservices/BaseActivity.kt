package com.aghourservices

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.settings.ThemePreference
import com.aghourservices.user.SignInActivity
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: LinearLayout

    fun showBottomNav() {
        bottomNavigationView = findViewById(R.id.linearBottomNavVIew)
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideBottomNav() {
        bottomNavigationView = findViewById(R.id.linearBottomNavVIew)
        bottomNavigationView.visibility = View.GONE
    }

    fun loadFragments(fragment: Fragment?, stacked: Boolean) {
        val backStateName: String = fragment?.javaClass.toString()
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()

        if (fragment != null) {
            ft.replace(R.id.fragmentContainerView, fragment)
            if (stacked) {
                ft.addToBackStack(backStateName)
            }
            ft.commit()
        }
    }

    fun shareApp() {
        Event.sendFirebaseEvent("Share_App", "")
        val shareText = getString(R.string.shareText)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "مشاركة التطبيق"))
    }

    fun rateApp() {
        Event.sendFirebaseEvent("Rate", "")
        val url = "https://play.google.com/store/apps/details?id=com.aghourservices"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun facebook() {
        Event.sendFirebaseEvent("Facebook_Page", "")
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb:/page/110004384736318")))
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebook.com/aghourservices")
                )
            )
        }
    }

    fun whatsApp(number: String) {
        Event.sendFirebaseEvent("Whats_App", "")
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun gmail() {
        Event.sendFirebaseEvent("Gmail_App", "")
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(getString(R.string.gmail_uri))
        }
        startActivity(emailIntent)
    }

    private fun logOut() {
        Event.sendFirebaseEvent("Sign_Out", "")
        UserInfo().clearUserData(this)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    fun showOnCloseDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(R.string.title)
        alertDialogBuilder.setMessage(R.string.message)
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(R.string.positiveButton) { _, _ ->
            logOut()
        }
        alertDialogBuilder.setNegativeButton(R.string.negativeButton) { _, _ ->
            Event.sendFirebaseEvent("STAY_ACTION", "")
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 16f
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 16f

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextAppearance(R.style.SegoeTextBold)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextAppearance(R.style.SegoeTextBold)
        }
    }
}