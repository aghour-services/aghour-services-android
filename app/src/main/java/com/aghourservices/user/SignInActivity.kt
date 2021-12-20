package com.aghourservices.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adView = findViewById(R.id.adView)
        AghourAdManager.displayBannerAd(this, adView)

        binding.btnLogin.setOnClickListener {
            if (binding.email.text.toString().trim().isEmpty() || binding.password.text.toString()
                    .trim()
                    .isEmpty()
            ) {
                binding.email.error = "ادخل بريدك الالكتروني"
                binding.password.error = "اكتب كلمة السر"
            } else {
                val email = binding.email.toString()
                val password = binding.password.toString()
                val user = User("", "", email, password, "")
                loginUser(user)
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.btnUseApp.setOnClickListener {
            binding.progressBarLogin.visibility = View.VISIBLE
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(user: User) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(SignInService::class.java)
        val retrofitData = retrofitBuilder.signIn(user.userObject())

        retrofitData.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() != 200) {
                    Log.e("Error", response.code().toString())
                    Toast.makeText(
                        this@SignInActivity, "خطأ في التسجيل برجاء اعادة المحاولة",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                val userInfo = UserInfo()
                userInfo.saveUserData(this@SignInActivity, user)
                startActivity(Intent(this@SignInActivity, CategoriesActivity::class.java))
            }

            @SuppressLint("ShowToast")
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(
                    this@SignInActivity, "تأكد من اتصالك بالانترنت",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }
}