package com.aghourservices

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aghourservices.about.AboutFragment
import com.aghourservices.ads.Banner
import com.aghourservices.ads.Interstitial
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.databinding.ActivityMainBinding
import com.aghourservices.firms.AddDataFragment
import com.aghourservices.news.NewsFragment
import com.aghourservices.search.SearchActivity
import com.aghourservices.user.SignUpActivity
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var btnRegister: Button
    private lateinit var userDataView: LinearLayout
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userMobile: TextView
    private var selectedItemId = -1
    lateinit var toolbar: Toolbar
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkUser()
        hideNavItem()

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

        toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                if (selectedItemId == -1) {
                    return
                }
                when (selectedItemId) {
                    R.id.nav_home -> {
                        replaceFragment(CategoriesFragment(), false)
                    }
                    R.id.nav_news -> {
                        replaceFragment(NewsFragment(), false)
                    }
                    R.id.nav_add_firm -> {
                        replaceFragment(AddDataFragment(), true)
                    }
                    R.id.nav_faceBook -> {
                        facebook()
                    }
                    R.id.nav_log -> {
                        showOnCloseDialog()
                    }
                }
                selectedItemId = -1
            }
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null
        replaceFragment(NewsFragment(), false)
    }

    override fun setTitle(title: CharSequence?) {
        binding.toolBarTv.text = title
    }

    fun replaceFragment(fragment: Fragment, stacked: Boolean) {
        val backStateName: String = fragment.javaClass.toString()
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        ft.replace(R.id.fragmentContainerView, fragment)
        if (stacked) {
            ft.addToBackStack(backStateName)
        }
        ft.commit()
    }

    private fun checkUser() {
        val headerView: View = binding.navView.getHeaderView(0)
        userDataView = headerView.findViewById(R.id.user_data_view)
        btnRegister = headerView.findViewById(R.id.btn_register)
        userName = headerView.findViewById(R.id.user_name)
        userMobile = headerView.findViewById(R.id.user_mobile)
        userEmail = headerView.findViewById(R.id.user_email)
        val userInfo = UserInfo()
        if (userInfo.isUserLoggedIn(this@MainActivity)) {
            btnRegister.visibility = View.GONE
            userDataView.visibility = View.VISIBLE
            val user = userInfo.getUserData(this@MainActivity)
            userName.text = user.name
            userMobile.text = user.mobile
            userEmail.text = user.email
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("InflateParams", "ResourceType")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.searchIcon -> {
                startActivity(Intent(this, SearchActivity::class.java))
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                )
            }
            R.id.moreIcon -> {
                val dialog = BottomSheetDialog(this)
                val view = layoutInflater.inflate(R.layout.bottom_dialog_sheet, null)
                val btnClose = view.findViewById<Button>(R.id.btnDismiss)
                val share = view.findViewById<LinearLayout>(R.id.share)
                val rate = view.findViewById<LinearLayout>(R.id.rate)
                val aboutApp = view.findViewById<LinearLayout>(R.id.about)

                btnClose.setOnClickListener {
                    dialog.dismiss()
                }

                share.setOnClickListener {
                    shareApp()
                    dialog.dismiss()
                }

                rate.setOnClickListener {
                    rateApp()
                    dialog.dismiss()
                }
                aboutApp.setOnClickListener {
                    replaceFragment(AboutFragment(), true)
                    dialog.dismiss()
                }
                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
            }
        }

        when {
            toggle.onOptionsItemSelected(item) -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(Gravity.LEFT, true)
        selectedItemId = item.itemId
        return true
    }

    private fun hideNavItem() {
        val isUserLogin = UserInfo().isUserLoggedIn(this)
        if (isUserLogin) {
            val navView: Menu = binding.navView.menu
            navView.findItem(R.id.nav_add_firm).isVisible = true
            navView.findItem(R.id.nav_log).isVisible = true
        } else {
            val navView: Menu = binding.navView.menu
            navView.findItem(R.id.nav_add_firm).isVisible = false
            navView.findItem(R.id.nav_log).isVisible = false
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
        } else {
            val fragment = supportFragmentManager.fragments.last() as BaseFragment
            if (!fragment.onBackPressed()) {
                super.onBackPressed()
            }
        }
    }
}