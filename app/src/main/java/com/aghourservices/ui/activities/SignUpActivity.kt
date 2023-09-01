package com.aghourservices.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aghourservices.R
import com.aghourservices.data.model.User
import com.aghourservices.data.network.RetrofitInstance.userApi
import com.aghourservices.databinding.ActivitySignUpBinding
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.AlertDialogs.Companion.errorLogin
import com.aghourservices.utils.helper.AlertDialogs.Companion.noInternet
import com.aghourservices.utils.helper.Constants
import com.aghourservices.utils.helper.Intents
import com.aghourservices.utils.helper.ProgressDialog
import com.aghourservices.utils.services.cache.UserInfo.saveUserData
import com.google.android.gms.ads.AdView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var adView: AdView
    private lateinit var permissions: Array<String>
    private val progressDialog by lazy { ProgressDialog(this) }
    private var avatarUri: Uri? = null
    private var avatarPart: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEditTextIconsColor()
        initUserValidations()
        initUserLogin()
        adView()
        initPermissions()
        requestPermissions()
        initUserClicks()
    }

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun initEditTextIconsColor() {
        binding.apply {
            nameEdt.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    nameLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.colorPrimary
                            )
                        )
                    )
                } else {
                    nameLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.start_icon_tint
                            )
                        )
                    )
                }
            }

            phoneEdt.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    phoneLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.colorPrimary
                            )
                        )
                    )
                } else {
                    phoneLayout.setStartIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                applicationContext, R.color.start_icon_tint
                            )
                        )
                    )
                }
            }

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

    private fun initUserValidations() {
        binding.registerBtn.setOnClickListener {
            val name = binding.nameEdt.text.toString().trim()
            val mobile = binding.phoneEdt.text.toString().trim()
            val email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.apply {
                    emailLayout.error = getString(R.string.invalid_email)
                    emailEdt.isFocusable = true
                }
            } else if (password.length < 6 || password.isEmpty()) {
                binding.apply {
                    passwordLayout.error = getString(R.string.invalid_password)
                    passwordEdt.isFocusable = true
                }
            } else if (name.isEmpty()) {
                binding.apply {
                    nameLayout.error = getString(R.string.invalid_name)
                    nameEdt.isFocusable = true
                }
            } else {
                val user = User(null, name, mobile, email, password)
                createUser(user)
            }
        }

        binding.skipAccountBtn.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    private fun initUserLogin() {
        val havAccountText = binding.havAccountTv
        val spannableString = SpannableString(havAccountText.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary)
                ds.isUnderlineText = true
            }
        }
        val recoverWord = getString(R.string.signIn)
        val start = havAccountText.text.indexOf(recoverWord)
        val end = start + recoverWord.length
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        havAccountText.text = spannableString
        havAccountText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun initUserClicks() {
        binding.avatarImage.setOnClickListener {
            if (!checkStoragePermission()) {
                requestPermissions()
            } else {
                openGallery()
            }
        }
        binding.addUserImage.setOnClickListener {
            if (!checkStoragePermission()) {
                requestPermissions()
            } else {
                openGallery()
            }
        }
    }

    private fun createUser(user: User) {
        progressDialog.show(getString(R.string.creating_account))
        val nameBody = createRequestBody(user.name)
        val emailBody = createRequestBody(user.email)
        val mobileBody = createRequestBody(user.mobile)
        val passwordBody = createRequestBody(user.password)

        val retrofitBuilder =
            userApi.signUp(nameBody, emailBody, mobileBody, passwordBody, avatarPart)

        retrofitBuilder.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() != 201) {
                    errorLogin(this@SignUpActivity)
                    progressDialog.hide()
                    return
                }
                val responseUser = response.body() as User
                saveUserData(this@SignUpActivity, responseUser)
                startActivity(Intent(this@SignUpActivity, DashboardActivity::class.java))
                progressDialog.hide()
                finish()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                noInternet(this@SignUpActivity)
                progressDialog.hide()
            }
        })
    }

    private fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
    }

    private fun openGallery() {
        val galleryIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK)
        }
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, Constants.GALLERY_CODE)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            avatarUri = data?.data!!
            val file = File(Intents.getRealPathFromURI(this, avatarUri!!)!!)
            Intents.compressFile(this, file)
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            avatarPart =
                MultipartBody.Part.createFormData("user[avatar]", file.name, requestBody)
            binding.avatarImage.setImageURI(avatarUri)
        }
    }

    private fun initPermissions() {
        permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, Constants.REQUEST_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == (PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}