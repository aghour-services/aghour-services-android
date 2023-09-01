package com.aghourservices.ui.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.model.User
import com.aghourservices.data.network.RetrofitInstance
import com.aghourservices.databinding.ActivityFullScreenProfileBinding
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullScreenProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenProfileBinding
    private val user by lazy { getUserData(this) }
    private val bundle by lazy { intent.extras }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fullScreenActivity()

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        Log.d("BUNDLE", bundle?.getInt("id").toString())

        if (bundle != null) {
            userProfile()
        } else {
            currentUserProfile()
        }
    }

    private fun fullScreenActivity() {
        supportActionBar?.hide()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }

    private fun currentUserProfile() {
        val retrofitInstance = RetrofitInstance.userApi.userProfile(user.token)

        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {

                        Glide.with(this@FullScreenProfileActivity)
                            .load(profile.url)
                            .placeholder(R.mipmap.user)
                            .error(R.mipmap.user)
                            .encodeQuality(100)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.avatarImage)

                        binding.userName.apply {
                            text = profile.name
                            if (profile.verified == false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                            }
                            visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@FullScreenProfileActivity, "لا يوجد إنترنت", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun userProfile() {
        val userId = bundle?.getInt("id")
        val retrofitInstance = RetrofitInstance.userApi.show(userId!!, getFCMToken(this))

        retrofitInstance.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        Glide.with(this@FullScreenProfileActivity)
                            .load(user.url)
                            .placeholder(R.mipmap.user)
                            .error(R.mipmap.user)
                            .encodeQuality(100)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.avatarImage)

                        binding.userName.apply {
                            text = user.name
                            if (!user.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                            }
                            visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@FullScreenProfileActivity, "لا يوجد إنترنت", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}