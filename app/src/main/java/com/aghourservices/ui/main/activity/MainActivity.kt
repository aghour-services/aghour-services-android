package com.aghourservices.ui.main.activity

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.ActivityMainBinding
import com.aghourservices.databinding.BottomSheetBinding
import com.aghourservices.ui.fragment.CategoriesFragmentDirections
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.main.cache.UserInfo.saveUserID
import com.aghourservices.utils.ads.Interstitial
import com.aghourservices.utils.helper.Constants.Companion.APP_UPDATE_REQUEST_CODE
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val interstitial = Interstitial()
    private var reviewManager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupDialogForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                        this
                    )
                    else -> Log.d(
                        "UpdateCheck",
                        "InstallStateUpdatedListener: state: ${installState.installStatus()}"
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mainNavController = setupNavController()
        checkExtras(mainNavController)
        floatActionButton()
        inAppRating()
        inAppUpdate()
        getUserProfile()
    }

    private fun getUserProfile() {
        val userData = getUserData(this)
        val retrofitInstance = RetrofitInstance(this).userApi.userProfile(userData.token)
        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                val profile = response.body()

                if (response.isSuccessful) {
                    saveUserID(this@MainActivity, profile?.id!!)
                    Log.d("Profile", "onResponse: ${profile?.id}")
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.d("user", t.message.toString())
            }
        })
    }

    private fun inAppRating() {
        reviewManager = ReviewManagerFactory.create(this)
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }
        }
//        TODO: Just for testing
//        binding.button.setOnClickListener {
//            val flow = reviewManager!!.launchReviewFlow(
//                this@MainActivity,
//                reviewInfo!!
//            )
//            flow.addOnCompleteListener { }
//        }
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

    private fun inAppUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    val installType = when {
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
                        else -> null
                    }
                    if (installType == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(
                        appUpdatedListener
                    )

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        installType!!,
                        this,
                        APP_UPDATE_REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun popupDialogForCompleteUpdate() {
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        alertDialogBuilder.setTitle("التطبيق جاهز للتثبيت")
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
        alertDialogBuilder.setPositiveButton("تثبيت الأن") { _, _ ->
            appUpdateManager.completeUpdate()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.setCancelable(false)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 14f
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupDialogForCompleteUpdate()
            }
            try {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        APP_UPDATE_REQUEST_CODE
                    )
                }
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        }
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
            val binding = BottomSheetBinding.inflate(layoutInflater)

            val bottomSheetDialog = BottomSheetDialog(this).apply {
                setContentView(binding.root)
                setCancelable(true)
                show()
            }

            binding.addDataBtn.setOnClickListener {
                startActivity(Intent(this, AddDataActivity::class.java))
                bottomSheetDialog.dismiss()
            }
            binding.addArticleBtn.setOnClickListener {
                startActivity(Intent(this, AddArticleActivity::class.java))
                bottomSheetDialog.dismiss()
            }
            binding.dismissSheet.setOnClickListener {
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
        when (item.itemId) {
            R.id.settingActivity -> startActivity(Intent(this, SettingsActivity::class.java))
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