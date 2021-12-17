package com.aghourservices

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.cache.UserInfo
import com.aghourservices.user.LoginActivity
import com.aghourservices.search.SearchActivity
import com.aghourservices.user.SignupActivity
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
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    fun shareApp() {
        sendFirebaseEvent("Share", "")
        val shareText = "https://play.google.com/store/apps/details?id=com.aghourservices"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun rateApp() {
        val url = "https://play.google.com/store/apps/details?id=com.aghourservices"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    fun facebook() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb:/page/110004384736318"))
            startActivity(intent)
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
        UserInfo().clearUserData(this@BaseActivity)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
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