package com.aghourservices.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.categories.CategoriesActivity
import com.aghourservices.databinding.ActivityLoginBinding

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class LoginActivity : AppCompatActivity() {
    private var userName: String = "01287303441"
    private var pass: String = "m7madmagdy"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var phoneNumber: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        btnLogin.setOnClickListener {
            if (phoneNumber.text.toString().trim().isEmpty() || password.text.toString().trim()
                    .isEmpty()
            ) {
                phoneNumber.error = "اكتب رقم تليفونك"
                password.error = "اكتب كلمة السر"

            } else if (phoneNumber.text.toString() != userName || password.text.toString() != pass) {
                Toast.makeText(this, "بياناتك غلط", Toast.LENGTH_SHORT).show()

            } else if (phoneNumber.text.toString()
                    .isNotEmpty() && password.text.toString()
                    .isNotEmpty()
            )
                if (phoneNumber.text.toString() == userName && password.text.toString() == pass) {
                    val intent = Intent(this, CategoriesActivity::class.java)
                    startActivity(intent)
                }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        phoneNumber = findViewById(R.id.phoneNumber)
        password = findViewById(R.id.password)
        btnRegister = findViewById(R.id.btn_register)
    }
    override fun onBackPressed() {
        finishAffinity()
    }
}