package com.aghourservices

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.aghourservices.ads.Banner
import com.aghourservices.ads.Interstitial
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.databinding.ActivityMainBinding
import com.aghourservices.favorite.FavoriteFragment
import com.aghourservices.favorite.local.FavoriteDatabase
import com.aghourservices.firms.AddDataFragment
import com.aghourservices.news.NewsFragment
import com.aghourservices.search.SearchFragment
import com.aghourservices.settings.SettingFragment
import com.google.android.gms.ads.AdView
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var adView: AdView
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val interstitial = Interstitial()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragments(CategoriesFragment(), false)
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.show()
        val bottomNavView = binding.bottomView
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)

//        runnable = Runnable { interstitial.load(this@MainActivity) }
//        handler.post(runnable)

        bottomNavView.setOnItemSelectedListener {
            var fragment: Fragment? = null
            when (it.itemId) {
                R.id.home -> fragment = CategoriesFragment()
                R.id.search -> fragment = SearchFragment()
                R.id.news -> fragment = NewsFragment()
                R.id.addData -> fragment = AddDataFragment()
                R.id.settings -> fragment = SettingFragment()
            }
            loadFragments(fragment, true)
            true
        }
    }

    override fun onResume() {
        super.onResume()
//        handler.postDelayed(runnable, 120000)
        if (!checkForInternet(this)) {
            binding.notInternet.visibility = View.VISIBLE
        } else {
            binding.notInternet.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
//        handler.removeCallbacks(runnable)
    }

    /** Check Internet Connection **/
    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    override fun setTitle(title: CharSequence?) {
        binding.toolBarTv.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("InflateParams", "ResourceType")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                loadFragments(SettingFragment(), true)
            }
            R.id.favorite -> {
                loadFragments(FavoriteFragment(), true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.last() as BaseFragment
        if (!fragment.onBackPressed()) {
            super.onBackPressed()
        }
        /** selected Home fragment **/
        val selectedItemId = binding.bottomView.selectedItemId
        if (selectedItemId != R.id.home) {
            binding.bottomView.selectedItemId = R.id.home
        }
    }
}