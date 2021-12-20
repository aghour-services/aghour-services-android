package com.aghourservices

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.cache.UserInfo
import com.aghourservices.search.SearchActivity
import com.aghourservices.user.sign_in.SignInActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    protected fun sendFirebaseEvent(eventName: String, data: String) {
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(eventName) {
            param("data", data)
        }
    }

    private fun openSearchActivity() {
        startActivity(Intent(this, SearchActivity::class.java))
    }

    fun shareApp() {
        sendFirebaseEvent("Share", "")
        val shareText = "https://play.google.com/store/apps/details?id=com.aghourservices"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    fun rateApp() {
        sendFirebaseEvent("Rate", "")
        val url = "https://play.google.com/store/apps/details?id=com.aghourservices"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun facebook() {
        sendFirebaseEvent("Facebook_Page", "")
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

    private fun logOut() {
        sendFirebaseEvent("Sign_Out", "")
        UserInfo().clearUserData(this@BaseActivity)
        startActivity(Intent(this, SignInActivity::class.java))
    }

    fun showOnCloseDialog() {
        val title = "تسجيل الخروج !"
        val message = "متأكد انك عايز تسجل خروج ؟"
        val positiveButton = "نعم"
        val negativeButton = "لا"

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#59A5E1'>$positiveButton</font>")) { _, _ ->
            logOut()
            sendFirebaseEvent("ALERT_LOGOUT_ACTION", "")
        }
        alertDialogBuilder.setNegativeButton(Html.fromHtml("<font color='#59A5E1'>$negativeButton</font>")) { _, _ ->
            sendFirebaseEvent("ALERT_STAY_ACTION", "")
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchIcon -> {
                openSearchActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}