package com.aghourservices.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.databinding.ActivityRegisterBinding
import com.aghourservices.user.api.ApiServices
import com.aghourservices.user.api.User
import com.google.gson.JsonObject
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


        binding.btnRegister.setOnClickListener(View.OnClickListener {
            if (binding.name.text.toString().isEmpty() || binding.mobile.text.toString()
                    .isEmpty() || binding.password.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this@SignupActivity,
                    "Please enter both the values",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            var name = binding.name.text.toString()
            var mobile = binding.mobile.text.toString()
            var password = binding.password.text.toString()
            val user = User(name, mobile, password)
            createUser(user)
        })

        binding.loginTxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUser(user: User) {

        binding.idLoadingPB.visibility = View.VISIBLE

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val retrofitData = retrofitBuilder.createUser(user.userObject())
        Log.d("User", user.toString())

        retrofitData.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                binding.idLoadingPB.visibility = View.GONE

//                binding.name.setText("")
//                binding.mobile.setText("")
//                binding.password.setText("")
                response.code()

                val responseFromAPI = response.body()

//
                Toast.makeText(this@SignupActivity, response.code().toString(), Toast.LENGTH_LONG)
                    .show()

//                val responseString = """
//                        Response Code : ${response.code()}
//                        Name : ${responseFromAPI!!.name}
//                        Job : ${responseFromAPI.mobile}
//                        password : ${responseFromAPI.password}
//                        """.trimIndent()

//                binding.idTVResponse.text = responseString
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Error Found is", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}