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
    private var password: String = "m7madmagdy"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var phoneNumber: EditText
    private lateinit var pass: EditText
    private lateinit var btnLogin: Button
    private lateinit var txt_register: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("myShared", Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("u", "")
        val passWord = sharedPreferences.getString("p", "")
        phoneNumber.setText(user)
        pass.setText(passWord)
        btnLogin.setOnClickListener {

            if (phoneNumber.text.toString().isEmpty() || pass.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(this, "اكتب بياناتك الأول يا نجم", Toast.LENGTH_SHORT).show()
            } else if (phoneNumber.text.toString() != userName || pass.text.toString() != password) {
                Toast.makeText(this, "البيانات غلط", Toast.LENGTH_SHORT).show()
            } else if (phoneNumber.text.toString()
                    .isNotEmpty() && pass.text.toString()
                    .isNotEmpty()
            )
                if (phoneNumber.text.toString() == userName && pass.text.toString() == password) {

                    val shared: SharedPreferences =
                        getSharedPreferences("myShared", Context.MODE_PRIVATE)
                    val editor = shared.edit()
                    editor.putString("u", phoneNumber.text.toString())
                    editor.putString("p", pass.text.toString())
                    editor.apply()

                    val intent = Intent(this, CategoriesActivity::class.java)
                    intent.putExtra("app", "Simple RecyclerView")
                    startActivity(intent)
                }
        }

        txt_register.setOnClickListener {
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        phoneNumber = findViewById(R.id.phoneNumber)
        pass = findViewById(R.id.password)
        txt_register = findViewById(R.id.txt_register)
    }
}