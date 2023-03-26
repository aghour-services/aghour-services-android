package com.aghourservices.ui.main.activity

import android.Manifest
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
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.showProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import com.google.android.gms.ads.AdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddArticleActivity : AppCompatActivity(), ShowSoftKeyboard {
    private lateinit var binding: ActivityAddArticleBinding
    private lateinit var adView: AdView
    private val isUserLogin by lazy { isUserLoggedIn(this@AddArticleActivity) }
    private val user by lazy { getUserData(this@AddArticleActivity) }
    private lateinit var permissions: Array<String>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showKeyboard(this, binding.articleEdt)
        initUserClick()
        adView()
        initPermissions()
        requestPermissions()
        initUserClicks()
    }

    private fun initUserClicks(){
        binding.addImageBtn.setOnClickListener {
            if (!checkStoragePermission()) {
                requestPermissions()
            } else {
                openGallery()
            }
        }
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
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GALLERY_CODE -> {
                    imageUri = data?.data!!
                    binding.articleImg.setImageURI(imageUri)
                    binding.addImageBtn.text = "تغيير الصورة"
                    Log.d("URI", "onActivityResult: $imageUri")
                }
            }
        }
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

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
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

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun initUserClick() {
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
                                AlertDialog.createAccount(this@AddArticleActivity, "لإضافة خبر أعمل حساب الأول")
                            }else{
                                val article = Article()
                                publishArticle(article)
                            }
                        }
                    }
                }
            })
        }
    }
    private fun publishArticle(article: Article) {
        showProgressDialog(this@AddArticleActivity)
        article.description = binding.articleEdt.text.toString().trim()
        createArticle(article)
    }

    private fun createArticle(article: Article) {
        val user = getUserData(this)
        val retrofitBuilder = RetrofitInstance(this).newsApi.createArticle(
            article.toJsonObject(),
            user.token,
            UserInfo.getFCMToken(this)
        )

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(
                call: Call<Article>,
                response: Response<Article>
            ) {
                if (getProfile()){
                    Toast.makeText(this@AddArticleActivity, "تم إضافة الخبر علطول عشان انت أدمن", Toast.LENGTH_SHORT).show()
                }else{
                    AlertDialog.dataAdded(this@AddArticleActivity)
                }
                setTextEmpty()
            }

            override fun onFailure(
                call: Call<Article>,
                t: Throwable
            ) {
                AlertDialog.noInternet(this@AddArticleActivity)
                hideProgressDialog()
            }
        })
    }

    private fun getProfile(): Boolean {
        var profile = Profile()
        val retrofitInstance = RetrofitInstance(this).userApi.userProfile(user.token)
        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(
                call: Call<Profile>,
                response: Response<Profile>
            ) {
                profile = response.body()!!
            }

            override fun onFailure(
                call: Call<Profile>,
                t: Throwable
            ) {
                AlertDialog.noInternet(this@AddArticleActivity)
            }
        })
        return profile.is_verified
    }

    private fun setTextEmpty() {
        binding.articleEdt.text!!.clear()
        hideProgressDialog()
    }
}