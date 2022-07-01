package com.aghourservices.ui.app.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.data.model.User
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.ActivitySignInBinding
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

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adView = findViewById(R.id.adView)
        Banner.show(this, adView)

        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val valid = email.isEmpty() || password.isEmpty()

            if (valid) {
                binding.email.error = "ادخل بريدك الالكتروني"
                binding.password.error = "اكتب كلمة السر"
            } else {
                val user = User("", "", email, password, "")
                loginUser(user)
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        binding.btnUseApp.setOnClickListener {
            val settings = Settings()
            showProgressDialog(this)
            sendFirebaseEvent("SKIP_LOGIN", "")
            settings.doNotShowRigsterActivity(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loginUser(user: User) {
        showProgressDialog(this)
        val retrofitBuilder =
            RetrofitInstance(this).retrofit.create(com.aghourservices.data.api.UserApi::class.java)
        val retrofitData = retrofitBuilder.signIn(user.userObject())

        retrofitData.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() != 200) {
                    errorLogin(this@SignInActivity)
                    hideProgressDialog()
                    return
                }
                val userInfo = UserInfo()
                val responseUser = response.body() as User
                userInfo.saveUserData(this@SignInActivity, responseUser)
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                hideProgressDialog()
                finish()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                noInternet(this@SignInActivity)
                hideProgressDialog()
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }
}