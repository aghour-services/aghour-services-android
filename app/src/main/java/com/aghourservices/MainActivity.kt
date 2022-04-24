package com.aghourservices

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aghourservices.ads.Banner
import com.aghourservices.ads.Interstitial
import com.aghourservices.check_network.CheckNetworkLiveData
import com.aghourservices.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adView: AdView
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val interstitial = Interstitial()

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.show()
        val bottomNavView = binding.bottomView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.setupWithNavController(navController)
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
        runnable = Runnable { interstitial.load(this@MainActivity) }
        handler.post(runnable)

        //Generate Device Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("TAG", token)
        })

        val intent = intent
        val message = intent.getStringExtra("message")
        if(!message.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("إشـعـار")
                .setMessage(message)
                .setPositiveButton("تـمـام") { _, _ -> }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 120000)

        val checkNetworkLiveData = CheckNetworkLiveData(application)
        checkNetworkLiveData.observe(this) { isConnected ->
            binding.notInternet.isVisible = !isConnected
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun setTitle(title: CharSequence?) {
        binding.toolBarTv.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingFragment) {
            val navController: NavController = Navigation.findNavController(this@MainActivity, R.id.fragmentContainerView)
            navController.navigate(R.id.settingFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this@MainActivity, R.id.fragmentContainerView)
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}