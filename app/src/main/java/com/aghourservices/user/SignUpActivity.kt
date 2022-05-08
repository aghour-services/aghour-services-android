package com.aghourservices.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.cache.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.aghourservices.cache.Settings
import com.aghourservices.databinding.ActivitySignUpBinding
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import android.app.Dialog
import com.aghourservices.MainActivity
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.constants.RetrofitInstance
import com.aghourservices.interfaces.AlertDialog.Companion.errorLogin
import com.aghourservices.interfaces.AlertDialog.Companion.noInternet
import com.aghourservices.user.api.SignUpService
import com.google.android.gms.ads.AdView

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var adView: AdView
    private var progressDialog: Dialog? = null

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
            showProgressDialog()
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
        showProgressDialog()
        val retrofitBuilder = RetrofitInstance(this).retrofit.create(SignUpService::class.java)
        val retrofitData = retrofitBuilder.signUp(user.userObject())

        retrofitData.enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() != 201) {
                    errorLogin(this@SignUpActivity)
                    hideProgressDialog()
                    return
                }
                val userInfo = UserInfo()
                val responseUser = response.body() as User
                userInfo.saveUserData(this@SignUpActivity, responseUser)
                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                hideProgressDialog()
                finish()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                noInternet(this@SignUpActivity)
                hideProgressDialog()
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

    private fun showProgressDialog(){
        progressDialog = Dialog(this)
        progressDialog!!.setContentView(R.layout.dialog_custom_progress)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideProgressDialog(){
        if(progressDialog != null)
            progressDialog!!.dismiss()
    }

    override fun onBackPressed() {
        finish()
    }
}