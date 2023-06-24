package com.aghourservices.ui.activites

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance.userApi
import com.aghourservices.databinding.ActivityDashboardBinding
import com.aghourservices.databinding.BottomSheetBinding
import com.aghourservices.ui.fragments.CategoriesFragmentDirections
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.aghourservices.utils.services.cache.UserInfo.saveFCMToken
import com.aghourservices.utils.services.cache.UserInfo.saveProfile
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.preferences.RatingThreshold
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {
    private var _binding: ActivityDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mainNavController = setupNavController()
        checkExtras(mainNavController)
        getUserProfile()
        floatActionButton()
        inAppRating()
        inAppUpdate()
        notificationPermission()
        getFirebaseInstanceToken()
    }

    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                99
            )
        }
    }

    private fun getUserProfile() {
        val userData = getUserData(this)
        val retrofitInstance = userApi.userProfile(userData.token)
        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                val profile = response.body()

                if (response.isSuccessful) {
                    saveProfile(this@DashboardActivity, profile?.id!!, profile.name, profile.is_verified)
                    Log.d("Profile", "onResponse: ${profile.id}")
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.d("user", t.message.toString())
            }
        })
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

    private fun inAppRating() {
        AppRating.Builder(this)
            .setMinimumLaunchTimes(3)
            .setMinimumDays(3)
            .useGoogleInAppReview()
            .setMinimumLaunchTimesToShowAgain(10)
            .setMinimumDaysToShowAgain(7)
            .setRatingThreshold(RatingThreshold.FOUR)
            .showIfMeetsConditions()

    }

    private fun inAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= 3
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    0
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                inAppUpdate()
            }
        }
    }

    private fun setupNavController(): NavController {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.show()

        val bottomNavView = binding.bottomView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.setupWithNavController(navController)

        /** Show the Up button in the action bar. **/
        NavigationUI.setupActionBarWithNavController(this, navController)

        return navController
    }

    private fun floatActionButton() {
        binding.addDataBtn.setOnClickListener {
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
                startActivity(Intent(this, CreateArticleActivity::class.java))
                bottomSheetDialog.dismiss()
            }
            binding.dismissSheet.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun getFirebaseInstanceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            saveFCMToken(this, token)
        })
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
            R.id.searchFragment -> {
                val navController =
                    Navigation.findNavController(this@DashboardActivity, R.id.fragmentContainerView)
                navController.navigate(R.id.searchFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            Navigation.findNavController(this@DashboardActivity, R.id.fragmentContainerView)
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}