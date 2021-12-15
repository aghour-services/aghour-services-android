package com.aghourservices.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.databinding.ActivityRegisterBinding
import com.aghourservices.user.api.ApiServices
import com.aghourservices.user.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegister.setOnClickListener {
            var name = binding.name.text.toString()
            var mobile = binding.mobile.text.toString()
            var password = binding.password.text.toString()
            val user = User(name, mobile, password)
            createUser(user)
            Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_LONG).show()
        }

        binding.loginTxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUser(user: User) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val retrofitData = retrofitBuilder.createUser(user)

        retrofitData.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("Create", response.body().toString())
                Toast.makeText(this@SignupActivity, response.body().toString(), Toast.LENGTH_LONG)
                    .show()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("Create", t.message.toString())
            }
        })
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}