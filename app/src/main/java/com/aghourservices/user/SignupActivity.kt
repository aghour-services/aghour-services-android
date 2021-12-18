package com.aghourservices.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesActivity
import com.aghourservices.databinding.ActivityRegisterBinding
import com.aghourservices.user.api.ApiServices
import com.aghourservices.user.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.aghourservices.cache.Settings
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreate.setOnClickListener(View.OnClickListener {
            if (binding.name.text.toString().trim().isEmpty() || binding.mobile.text.toString()
                    .trim()
                    .isEmpty() || binding.password.text.toString().trim().isEmpty()
            ) {
                binding.name.error = "اكتب اسمك"
                binding.mobile.error = "اكتب رقم موبايلك"
                binding.password.error = "اكتب كلمة السر الجديدة"

                return@OnClickListener

            } else {
                val name = binding.name.text.toString()
                val mobile = binding.mobile.text.toString()
                val password = binding.password.text.toString()
                val user = User(name, mobile, password)
                createUser(user)
            }
        })

        binding.btnUseApp.setOnClickListener {
            binding.progressBarRigster.visibility = View.VISIBLE
            startActivity(Intent(this, CategoriesActivity::class.java))
            doNotShowAgain()
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun createUser(user: User) {

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val retrofitData = retrofitBuilder.createUser(user.userObject())
        Log.d("User", user.toString())

        retrofitData.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {

                if (response.code() != 201) {
                    Toast.makeText(
                        this@SignupActivity, "خطأ في التسجيل برجاء اعادة المحاولة",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                binding.progressBarRigster.visibility = View.VISIBLE
                val userInfo = UserInfo()
                userInfo.saveUserData(this@SignupActivity, user)
                val intent = Intent(this@SignupActivity, CategoriesActivity::class.java)
                startActivity(intent)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Error", Toast.LENGTH_LONG).show()
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