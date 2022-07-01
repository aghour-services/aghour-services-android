package com.aghourservices.ui.app.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.ActivitySignUpBinding
import com.aghourservices.data.model.User
import com.aghourservices.ui.app.cache.Settings
import com.aghourservices.ui.app.cache.UserInfo
import com.aghourservices.ui.app.main.activity.MainActivity
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.Event.Companion.sendFirebaseEvent
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.showProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog.Companion.errorLogin
import com.aghourservices.utils.interfaces.AlertDialog.Companion.noInternet
import com.google.android.gms.ads.AdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUser()
        adView()
    }

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun initUser() {
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
                val user = User(name, mobile, email, password)
                createUser(user)
            }
        })

        binding.btnUseApp.setOnClickListener {
            val settings = Settings()
            showProgressDialog(this)
            sendFirebaseEvent("SKIP_LOGIN", "")
            settings.doNotShowRigsterActivity(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun createUser(user: User) {
        showProgressDialog(this)
        val retrofitBuilder =
            RetrofitInstance(this).retrofit.create(com.aghourservices.data.api.UserApi::class.java)
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

    override fun onBackPressed() {
        finish()
    }
}