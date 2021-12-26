package com.aghourservices

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils.replace
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import com.aghourservices.about.AboutFragment
import com.aghourservices.cache.UserInfo
import com.aghourservices.databinding.ActivityMainBinding
import com.aghourservices.firms.AddFirm
import com.aghourservices.user.SignUpActivity
import com.google.android.material.navigation.NavigationView


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private lateinit var btnRegister: Button
    private lateinit var userDataView: LinearLayout
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userMobile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        checkUser()
        hideNavLogout()
        hideAddItem()

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null
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
            userName.text = user.name.toString()
            userMobile.text = user.mobile
            userEmail.text = user.email
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    //LoadCategoriesList With RetrofitBuilder


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when {
            toggle.onOptionsItemSelected(item) -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("WrongConstant")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_share -> {
                shareApp()
            }
            R.id.nav_rate -> {
                rateApp()
            }
            R.id.nav_faceBook -> {
                facebook()
            }
            R.id.nav_log -> {
                showOnCloseDialog()
            }
            R.id.about_us -> {
                sendFirebaseEvent("About_App", "")
//                findNavController(R.id.fragmentContainerView).navigate(R.id.action_categoriesFragment_to_aboutFragment)
                supportFragmentManager.commit {
                    replace<AboutFragment>(R.id.fragmentContainerView)
                    setReorderingAllowed(true)
                    addToBackStack("About") // name can be null
                }
            }
            R.id.nav_add_firm -> {
                sendFirebaseEvent("Add_Firm", "")
                startActivity(Intent(this, AddFirm::class.java))
            }
        }
        binding.drawerLayout.closeDrawer(Gravity.START)
        return true
    }

    private fun hideNavLogout() {
        val isUserLogin = UserInfo().isUserLoggedIn(this)
        if (isUserLogin) {
            val navView: Menu = binding.navView.menu
            navView.findItem(R.id.nav_log).isVisible = true
        } else {
            val navView: Menu = binding.navView.menu
            navView.findItem(R.id.nav_log).isVisible = false
        }
    }

    private fun hideAddItem() {
        val isUserLogin = UserInfo().isUserLoggedIn(this)
        if (isUserLogin) {
            val navView: Menu = binding.navView.menu
            navView.findItem(R.id.nav_add_firm).isVisible = true
        } else {
            val navView: Menu = binding.navView.menu
            navView.findItem(R.id.nav_add_firm).isVisible = false
        }
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
            binding.drawerLayout.closeDrawer(Gravity.START)
        } else {
            super.onBackPressed()
        }
    }
}