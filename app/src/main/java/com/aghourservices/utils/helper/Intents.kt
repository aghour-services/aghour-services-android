package com.aghourservices.utils.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.ui.app.cache.UserInfo
import com.aghourservices.ui.app.user.SignInActivity

object Intents {

    fun shareApp(context: Context) {
        Event.sendFirebaseEvent("Share_App", "")
        val shareText = context.getString(R.string.shareText)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "مشاركة التطبيق"))
    }

    fun rateApp(context: Context) {
        Event.sendFirebaseEvent("Rate", "")
        val url = "https://play.google.com/store/apps/details?id=com.aghourservices"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun facebook(context: Context) {
        Event.sendFirebaseEvent("Facebook_Page", "")
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb:/page/110004384736318")))
        } catch (e: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebook.com/aghourservices")
                )
            )
        }
    }

    fun whatsApp(context: Context, number: String) {
        Event.sendFirebaseEvent("Whats_App", "")
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun gmail(context: Context) {
        Event.sendFirebaseEvent("Gmail_App", "")
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(context.getString(R.string.gmail_uri))
        }
        context.startActivity(emailIntent)
    }

    private fun logOut(context: Context) {
        Event.sendFirebaseEvent("Sign_Out", "")
        UserInfo().clearUserData(context)
        context.startActivity(Intent(context, SignInActivity::class.java))
        (context as AppCompatActivity).finishAffinity()
    }

    fun showOnCloseDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        alertDialogBuilder.setTitle(R.string.title)
        alertDialogBuilder.setMessage(R.string.message)
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(R.string.positiveButton) { _, _ ->
            logOut(context)
        }
        alertDialogBuilder.setNegativeButton(R.string.negativeButton) { _, _ ->
            Event.sendFirebaseEvent("STAY_ACTION", "")
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}