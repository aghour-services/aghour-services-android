package com.aghourservices

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.aghourservices.ads.Banner
import com.aghourservices.ads.Interstitial
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.databinding.ActivityMainBinding
import com.aghourservices.firms.AddDataFragment
import com.aghourservices.news.NewsFragment
import com.aghourservices.search.SearchFragment
import com.aghourservices.settings.SettingFragment
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.show()
        binding.bottomView.itemIconTintList = null

        adView = findViewById(R.id.adView)
        Banner.show(this, adView)

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                val interstitial = Interstitial()
                interstitial.load(this@MainActivity)
                mainHandler.postDelayed(this, 60000)
            }
        })
        loadFragments(CategoriesFragment(), false)
        bottomNavigationView = findViewById(R.id.bottomView)
        bottomNavigationView.setOnItemSelectedListener(this)
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null

        when (item.itemId) {
            R.id.home -> fragment = CategoriesFragment()
            R.id.search -> fragment = SearchFragment()
            R.id.news -> fragment = NewsFragment()
            R.id.addData -> fragment = AddDataFragment()
            R.id.settings -> fragment = SettingFragment()
            else -> {
                bottomNavigationView.visibility = View.GONE
            }
        }
        loadFragments(fragment, true)
        return true
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.fragments.last() as BaseFragment
        if (!fragment.onBackPressed()) {
            super.onBackPressed()
        } else if (bottomNavigationView.selectedItemId == R.id.home) {
        } else {
            bottomNavigationView.selectedItemId = R.id.home
            super.onBackPressed()
        }
    }
}