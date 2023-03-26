package com.aghourservices.ui.main.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.data.model.Profile
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.ActivityAddArticleBinding
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.main.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.Constants.Companion.GALLERY_CODE
import com.aghourservices.utils.helper.Constants.Companion.REQUEST_CODE
import com.aghourservices.utils.helper.ProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import com.google.android.gms.ads.AdView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddArticleActivity : AppCompatActivity(), ShowSoftKeyboard {
    private lateinit var binding: ActivityAddArticleBinding
    private lateinit var adView: AdView
    private val isUserLogin by lazy { isUserLoggedIn(this@AddArticleActivity) }
    private val user by lazy { getUserData(this@AddArticleActivity) }
    private lateinit var permissions: Array<String>
    private var imageUri: Uri? = null
    private var imagePart: MultipartBody.Part? = null
    private var isVerified: Boolean? = null
    private val progressDialog by lazy { ProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showKeyboard(this, binding.articleEdt)
        adView()
        initPermissions()
        requestPermissions()
        getUserProfile()
        initUserClick()
    }

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun openGallery() {
        val galleryIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK)
        }
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_CODE)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            val file = File(getRealPathFromURI(imageUri!!)!!)
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagePart =
                MultipartBody.Part.createFormData("article[attachment]", file.name, requestBody)
            binding.articleImg.setImageURI(imageUri)
            binding.addImageBtn.text = "تغيير الصورة"
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(projection[0])
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath
    }

    private fun initPermissions() {
        permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == (PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }

    private fun initUserClick() {
        binding.addImageBtn.setOnClickListener {
            if (!checkStoragePermission()) {
                requestPermissions()
            } else {
                openGallery()
            }
        }
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.apply {
            binding.articleEdt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val articleTxt = binding.articleEdt.text.toString().trim()

                    if (TextUtils.isEmpty(articleTxt)) {
                        binding.publishBtn.isEnabled = false
                    } else {
                        binding.publishBtn.isEnabled = true
                        binding.publishBtn.setOnClickListener {
                            if (!isUserLogin) {
                                AlertDialog.createAccount(
                                    this@AddArticleActivity,
                                    "لإضافة خبر أعمل حساب الأول"
                                )
                            } else {
                                createArticle(articleTxt)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun createArticle(description: String) {
        progressDialog.show("جاري إضافة الخبر")
        val user = getUserData(this)

        val descriptionBody =
            description.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())

        val retrofitBuilder = RetrofitInstance(this).newsApi.createArticle(
            user.token,
            UserInfo.getFCMToken(this),
            descriptionBody,
            imagePart
        )

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(
                call: Call<Article>,
                response: Response<Article>
            ) {
                if (response.isSuccessful) {
                    if (isVerified == true) {
                        Toast.makeText(this@AddArticleActivity, "تم إضافة الخبر", Toast.LENGTH_LONG).show()
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        AlertDialog.dataAdded(this@AddArticleActivity)
                    }
                    setTextEmpty()
                } else {
                    progressDialog.hide()
                }
            }

            override fun onFailure(
                call: Call<Article>,
                t: Throwable
            ) {
                AlertDialog.noInternet(this@AddArticleActivity)
                progressDialog.hide()
            }
        })
    }

    private fun getUserProfile() {
        val retrofitInstance = RetrofitInstance(this).userApi.userProfile(user.token)
        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(
                call: Call<Profile>,
                response: Response<Profile>
            ) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    isVerified = profile?.is_verified
                    Log.d("USER", "onResponse: $isVerified")
                    binding.userName.apply {
                        text = profile?.name
                        if (profile?.is_verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            tooltipText = context.getString(R.string.verified)
                        } else {
                            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<Profile>,
                t: Throwable
            ) {}
        })
    }

    private fun setTextEmpty() {
        binding.articleEdt.text!!.clear()
        progressDialog.hide()
    }
}