package com.aghourservices

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.search.SearchActivity


open class BaseActivity : AppCompatActivity() {

    private fun openSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun shareApp() {
        val shareText = "https://play.google.com/store/apps/details?id=com.aghourservices"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    //Share Button View and Create
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareButton -> {
                shareApp()
            }
            R.id.searchIcon -> {
                openSearchActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}