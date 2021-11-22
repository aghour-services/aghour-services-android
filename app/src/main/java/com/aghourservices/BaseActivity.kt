package com.aghourservices

import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.search.SearchActivity
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