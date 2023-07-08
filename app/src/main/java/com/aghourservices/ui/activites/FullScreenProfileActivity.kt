package com.aghourservices.ui.activites

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance
import com.aghourservices.databinding.ActivityFullScreenProfileBinding
import com.aghourservices.utils.helper.Intents.loadProfileImage
import com.aghourservices.utils.services.cache.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullScreenProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenProfileBinding
    private val user by lazy { UserInfo.getUserData(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fullScreenActivity()
        getProfile()

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fullScreenActivity() {
        supportActionBar?.hide()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }

    private fun getProfile() {
        val retrofitInstance = RetrofitInstance.userApi.userProfile(user.token)

        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {

                        loadProfileImage(
                            this@FullScreenProfileActivity,
                            profile.url,
                            binding.avatarImage
                        )

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
                binding.userName.apply {
                    text = user.name
                }
            }
        })
    }
}