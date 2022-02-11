package com.aghourservices

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.aghourservices.about.AboutFragment
import com.aghourservices.ads.Interstitial
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.databinding.ActivityMainBinding
import com.aghourservices.firms.AddDataFragment
import com.aghourservices.news.NewsFragment
import com.aghourservices.search.SearchFragment
import com.aghourservices.settings.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.show()
        binding.bottomView.itemIconTintList = null
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                val interstitial = Interstitial()
                interstitial.load(this@MainActivity)
                mainHandler.postDelayed(this, 60000)
            }
        })
        loadFragments(CategoriesFragment())
        bottomNavigationView = findViewById(R.id.bottomView)
        bottomNavigationView.setOnItemSelectedListener(this)
    }

    override fun setTitle(title: CharSequence?) {
        binding.toolBarTv.text = title
    }


    /**
    private fun replaceFragment(fragment: Fragment, stacked: Boolean) {
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
     **/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("InflateParams", "ResourceType")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
//            R.id.searchIcon -> {
//                startActivity(Intent(this, SearchActivity::class.java))
//                overridePendingTransition(
//                    R.anim.slide_in_right,
//                    R.anim.slide_out_left,
//                )
//            }
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
                    loadFragments(AboutFragment())
                    dialog.dismiss()
                }
                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
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
        }
        return loadFragments(fragment)
    }

    override fun onBackPressed() {

        /**
        val fragment = supportFragmentManager.fragments.last() as BaseFragment
        if (!fragment.onBackPressed()){
        super.onBackPressed()
        } **/

        if (bottomNavigationView.selectedItemId == R.id.home) {
            super.onBackPressed()
        } else {
            bottomNavigationView.selectedItemId = R.id.home
        }
    }
}