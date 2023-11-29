package com.aghourservices.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance.userApi
import com.aghourservices.databinding.ActivityDashboardBinding
import com.aghourservices.databinding.PostOrDataBottomSheetBinding
import com.aghourservices.ui.base.BaseActivity
import com.aghourservices.ui.viewModels.NotificationsViewModel
import com.aghourservices.utils.services.cache.UserInfo.saveFCMToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : BaseActivity() {
    private var _binding: ActivityDashboardBinding? = null
    private val binding get() = _binding!!
    private val notificationsViewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mainNavController = setupNavController()
        checkExtras(mainNavController, intent)
        floatActionButton()
        inAppRating()
        inAppUpdate()
        notificationPermission()
        getFirebaseInstanceToken()
        notificationsCount()

//        binding.profileImage.setOnClickListener {
//            startActivity(Intent(this@DashboardActivity, SettingsActivity::class.java))
//        }
    }

    override fun onResume() {
        super.onResume()
        getUserProfile()
        notificationsCount()
    }

    private fun notificationsCount() {
        notificationsViewModel.getNotifications(this, fcmToken, currentUser.token)
        notificationsViewModel.notificationsLiveData.observe(this) {
            notificationBadge()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun notificationBadge() {
        val badgeDrawable = BadgeDrawable.create(this).apply {
            isVisible = true
            backgroundColor = ContextCompat.getColor(this@DashboardActivity, R.color.clear)
        }
        BadgeUtils.attachBadgeDrawable(
            badgeDrawable,
            binding.toolbar,
            R.id.notificationsFragment
        )
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
        val retrofitInstance = userApi.userProfile(currentUser.token)
        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                val profile = response.body()

//                if (response.isSuccessful) {
//                    loadProfileImage(
//                        this@DashboardActivity,
//                        profile?.url.toString(),
//                        binding.profileImage
//                    )
//                } else {
//                    binding.profileImage.setImageResource(R.mipmap.user)
//                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
//                Log.d("user", t.message.toString())
//                binding.profileImage.setImageResource(R.mipmap.user)
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val mainNavController = setupNavController()
        checkExtras(mainNavController, intent)
    }

    private fun checkExtras(mainNavController: NavController, intent: Intent?) {
        val bundle = intent?.extras
        val articleId = bundle?.getString("article_id")

        if (articleId != null) {
            mainNavController.navigate(
                R.id.showOneArticleFragment,
                bundleOf("article_id" to articleId.toInt())
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode != RESULT_OK) {
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
            val binding = PostOrDataBottomSheetBinding.inflate(layoutInflater)

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
            binding.closeSheetBtn.setOnClickListener {
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
        super.setTitle(title)
        binding.toolbar.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchFragment -> {
                val mainNavController = setupNavController()
                mainNavController.navigate(R.id.searchFragment)
                overridePendingTransition(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop)
            }

            R.id.notificationsFragment -> {
                val mainNavController = setupNavController()
                mainNavController.navigate(R.id.notificationsFragment)
                overridePendingTransition(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop)
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