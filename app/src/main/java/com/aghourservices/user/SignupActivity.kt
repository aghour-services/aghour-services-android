package com.aghourservices.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
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
                var name = binding.name.text.toString()
                var mobile = binding.mobile.text.toString()
                var password = binding.password.text.toString()
                val user = User(name, mobile, password)
                createUser(user)
            }
        })

        binding.btnUseApp.setOnClickListener {
            binding.progressBarRigster.visibility = View.VISIBLE
            val intent = Intent(this,CategoriesActivity::class.java)
            startActivity(intent)
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
                var userInfo = UserInfo()
                userInfo.saveUserData(this@SignupActivity, user)
                var intent = Intent(this@SignupActivity, CategoriesActivity::class.java)
                startActivity(intent)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}