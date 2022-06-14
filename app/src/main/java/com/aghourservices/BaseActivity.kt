package com.aghourservices

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.cache.UserInfo
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.user.SignInActivity

open class BaseActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: LinearLayout

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
        finishAffinity()
    }

    fun showOnCloseDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context,R.style.AlertDialogTheme)
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
    }
}