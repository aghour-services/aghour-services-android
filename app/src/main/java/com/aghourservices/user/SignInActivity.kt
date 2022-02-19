package com.aghourservices.user

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.interfaces.AlertDialog.Companion.errorLogin
import com.aghourservices.interfaces.AlertDialog.Companion.noInternet
import com.aghourservices.MainActivity
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.cache.UserInfo
import com.aghourservices.databinding.ActivitySignInBinding
import com.aghourservices.user.api.SignInService
import com.google.android.gms.ads.AdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://aghour-services.magdi.work/api/users/"

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var adView: AdView
    private var progressDialog: Dialog? = null

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
            binding.progressBarUseApp.visibility = View.VISIBLE
            binding.txtUseApp.visibility = View.GONE
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loginUser(user: User) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(SignInService::class.java)
        val retrofitData = retrofitBuilder.signIn(user.userObject())

        showProgressDialog()

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

    private fun showProgressDialog(){
        progressDialog = Dialog(this)
        progressDialog!!.setContentView(R.layout.dialog_custom_progress)
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