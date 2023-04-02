package com.aghourservices.ui.main.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aghourservices.R
import com.aghourservices.data.model.User
import com.aghourservices.data.request.RetrofitInstance.userApi
import com.aghourservices.databinding.ActivitySignInBinding
import com.aghourservices.ui.main.cache.UserInfo.saveUserData
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.ProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog.Companion.errorLogin
import com.aghourservices.utils.interfaces.AlertDialog.Companion.noInternet
import com.google.android.gms.ads.AdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var adView: AdView
    private val progressDialog by lazy { ProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initIconsColor()
        initUserRegister()
        initUserClick()
        adView()
    }

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun initIconsColor() {
        binding.apply {
            emailEdt.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    emailLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.colorPrimary
                            )
                        )
                    )
                } else {
                    emailLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.start_icon_tint
                            )
                        )
                    )
                }
            }

            passwordEdt.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    passwordLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.colorPrimary
                            )
                        )
                    )
                } else {
                    passwordLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.start_icon_tint
                            )
                        )
                    )
                }
            }
        }
    }

    private fun initUserRegister() {
        val notHaveAccount = binding.notHavAccountTv
        val spannableString = SpannableString(notHaveAccount.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignInActivity, R.color.colorPrimary)
                ds.isUnderlineText = true
            }
        }
        val recoverWord = "أنشئ حساب"
        val start = notHaveAccount.text.indexOf(recoverWord)
        val end = start + recoverWord.length
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        notHaveAccount.text = spannableString
        notHaveAccount.movementMethod = LinkMovementMethod.getInstance()
    }


    private fun initUserClick() {
        binding.apply {
            loginBtn.setOnClickListener {
                val email = binding.emailEdt.text.toString().trim()
                val password = binding.passwordEdt.text.toString().trim()

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.apply {
                        emailLayout.error = getString(R.string.invalid_email)
                        emailEdt.isFocusable = true
                    }
                } else if (password.length < 6) {
                    binding.apply {
                        passwordLayout.error = getString(R.string.invalid_password)
                        passwordEdt.isFocusable = true
                    }
                } else {
                    val user = User(null, "", "", email, password, "")
                    loginUser(user)
                }
            }

            skipAccountBtn.setOnClickListener {
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun loginUser(user: User) {
        progressDialog.show(getString(R.string.logging_in))
        val retrofitBuilder = userApi.signIn(user.userObject())

        retrofitBuilder.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() != 200) {
                    errorLogin(this@SignInActivity)
                    progressDialog.hide()
                    return
                }
                val responseUser = response.body() as User
                saveUserData(this@SignInActivity, responseUser)
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                progressDialog.hide()
                finish()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                noInternet(this@SignInActivity)
                progressDialog.hide()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}