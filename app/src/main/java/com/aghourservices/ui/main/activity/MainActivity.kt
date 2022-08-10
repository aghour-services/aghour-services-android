package com.aghourservices.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.aghourservices.R
import com.aghourservices.databinding.ActivityMainBinding
import com.aghourservices.ui.fragment.CategoriesFragmentDirections
import com.aghourservices.utils.ads.Interstitial
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.myLooper()!!)
    private val interstitial = Interstitial()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        floatActionButton()

        val mainNavController = setupNavController()
        checkExtras(mainNavController)
        adView()
    }

    private fun checkExtras(mainNavController: NavController) {
        val newsTopic = getString(R.string.news_topic)
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                if (key == "from" && extras.get(key).toString().contains(newsTopic)) {
                    mainNavController.navigate(
                        CategoriesFragmentDirections.actionCategoriesFragmentToNewsFragment()
                    )
                }
            }
        }

        /** Check for notification foreground **/
        if (intent.getStringExtra("body") != null) {
            mainNavController.navigate(
                CategoriesFragmentDirections.actionCategoriesFragmentToNewsFragment()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 120000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun adView() {
        runnable = Runnable { interstitial.load(this@MainActivity) }
        handler.post(runnable)
    }

    private fun setupNavController(): NavController {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.show()

        val bottomNavView = binding.bottomView
        bottomNavView.background = null
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.setupWithNavController(navController)


        /** Show the Up button in the action bar. **/
        NavigationUI.setupActionBarWithNavController(this, navController)

        return navController
    }

    private fun floatActionButton() {

        binding.floatingActionButton.setOnClickListener {
            val dialogSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val addDataBtn = dialogSheet.findViewById(R.id.addDataBtn) as AppCompatTextView
            val addArticleBtn = dialogSheet.findViewById(R.id.addArticleBtn) as AppCompatTextView
            val dismissBtn = dialogSheet.findViewById(R.id.dismissSheet) as AppCompatImageButton

            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(dialogSheet)
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.show()

            addDataBtn.setOnClickListener {
                startActivity(Intent(this, AddDataActivity::class.java))
            }
            addArticleBtn.setOnClickListener {
                startActivity(Intent(this, AddArticleActivity::class.java))
            }
            dismissBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
    }

    override fun setTitle(title: CharSequence?) {
        binding.toolBarTv.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingActivity) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            Navigation.findNavController(this@MainActivity, R.id.fragmentContainerView)
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}