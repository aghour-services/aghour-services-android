package com.aghourservices.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.aghourservices.cache.Settings
import com.aghourservices.databinding.ActivitySignUpBinding
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import android.annotation.SuppressLint
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.user.api.SignUpService
import com.google.android.gms.ads.AdView

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adView = findViewById(R.id.adView)
        AghourAdManager.displayBannerAd(this, adView)

        binding.btnCreate.setOnClickListener(View.OnClickListener {
            if (binding.name.text.toString().trim().isEmpty() || binding.email.text.toString()
                    .trim().isEmpty()
                || binding.password.text.toString().trim().isEmpty()
            ) {
                binding.name.error = "اكتب اسمك"
                binding.email.error = "ادخل بريدك الالكتروني"
                binding.password.error = "اختر كلمة سر لا تقل عن 6 أحرف"

                return@OnClickListener

            } else {
                val name = binding.name.text.toString()
                val mobile = binding.mobile.text.toString()
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()
                val user = User(name, mobile, email, password, "")
                createUser(user)
            }
        })

        binding.btnUseApp.setOnClickListener {
            binding.progressBarRegister.visibility = View.VISIBLE
            startActivity(Intent(this, CategoriesActivity::class.java))
            doNotShowAgain()
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun createUser(user: User) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(SignUpService::class.java)
        val retrofitData = retrofitBuilder.signUp(user.userObject())

        retrofitData.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() != 201) {
                    Toast.makeText(
                        this@SignUpActivity, "خطأ في التسجيل برجاء اعادة المحاولة",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                binding.progressBarRegister.visibility = View.VISIBLE
                val userInfo = UserInfo()
                userInfo.saveUserData(this@SignUpActivity, user)
                startActivity(Intent(this@SignUpActivity, CategoriesActivity::class.java))
            }

            @SuppressLint("ShowToast")
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(
                    this@SignUpActivity, "تأكد من اتصالك بالانترنت",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun doNotShowAgain() {
        val settings = Settings()
        if (binding.doNotShowAgain.isChecked) {
            sendFirebaseEvent("SKIP_LOGIN", "")
            settings.doNotShowRigsterActivity(this)
        }
    }

    private fun sendFirebaseEvent(eventName: String, data: String) {
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(eventName) {
            param("data", data)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}