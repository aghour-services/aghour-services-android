package com.aghourservices.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.cache.UserInfo
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
import android.graphics.Color
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.aghourservices.MainActivity
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.interfaces.AlertDialog.Companion.errorLogin
import com.aghourservices.interfaces.AlertDialog.Companion.noInternet
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
        Banner.show(this, adView)

        binding.btnCreate.setOnClickListener(View.OnClickListener {
            val name = binding.name.text.toString()
            val mobile = binding.mobile.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val valid = name.isEmpty() || email.isEmpty() || password.isEmpty()
            if (valid) {
                binding.name.error = "اكتب اسمك"
                binding.email.error = "ادخل بريدك الالكتروني"
                binding.password.error = "اختر كلمة سر لا تقل عن 6 أحرف"
                return@OnClickListener
            } else {
                val user = User(name, mobile, email, password, "")
                createUser(user)
            }
        })

        binding.btnUseApp.setOnClickListener {
            binding.useAppTxt.visibility = View.GONE
            binding.progressBarUseApp.visibility = View.VISIBLE
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            doNotShowAgain()
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
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
                    errorLogin(this@SignUpActivity)
                    return
                }
                binding.accountTxtCreate.visibility =View.GONE
                binding.progressBarRegister.visibility = View.VISIBLE
                val userInfo = UserInfo()
                val responseUser = response.body() as User

                userInfo.saveUserData(this@SignUpActivity, responseUser)
                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                noInternet(this@SignUpActivity)
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

//    fun errorLogin() {
//        val alertDialogBuilder = AlertDialog.Builder(this)
//        alertDialogBuilder.setTitle(R.string.error_logIn)
//        alertDialogBuilder.setIcon(R.mipmap.cloud)
//        alertDialogBuilder.setCancelable(true)
//        alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
//        val alertDialog = alertDialogBuilder.create()
//        alertDialog.show()
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
//    }
//
//    fun noInternet() {
//        val alertDialogBuilder = AlertDialog.Builder(this)
//        alertDialogBuilder.setTitle(R.string.no_internet)
//        alertDialogBuilder.setIcon(R.mipmap.cloud)
//        alertDialogBuilder.setCancelable(true)
//        alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
//        val alertDialog = alertDialogBuilder.create()
//        alertDialog.show()
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
//    }

    override fun onBackPressed() {
        finish()
    }
}