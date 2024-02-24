package com.aghourservices.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance.userApi
import com.aghourservices.databinding.ActivityCreateArticleBinding
import com.aghourservices.ui.base.BaseActivity
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.helper.Constants.Companion.GALLERY_CODE
import com.aghourservices.utils.helper.Intents.compressImage
import com.aghourservices.utils.helper.Intents.getRealPathFromURI
import com.aghourservices.utils.helper.Intents.loadProfileImage
import com.aghourservices.utils.services.ArticleService
import com.aghourservices.utils.services.cache.UserInfo.saveProfile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CreateArticleActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateArticleBinding
    private var imageUri: Uri? = null
    private var imagePart: MultipartBody.Part? = null
    private var isVerified: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserProfile()
        initUserClick()
        adView()
        requestPermissions()
        binding.userLayout.isVisible = isUserLogin
    }

    private fun requestPermissions() {
        if (!checkStoragePermission()) {
            requestStoragePermissions()
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            val file = File(getRealPathFromURI(this, imageUri!!)!!)
            lifecycleScope.launch {
                val compressedImage = compressImage(this@CreateArticleActivity, file.path)
                val requestBody = compressedImage.asRequestBody("image/*".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("article[attachment]", compressedImage.name, requestBody)
                binding.articleImage.setImageURI(imageUri)
                binding.removeImg.isVisible = true
                binding.articleImage.isVisible = true
                binding.addImageBtn.text = "تغيير الصورة"
            }
        }
    }

    private fun initUserClick() {
        binding.addImageBtn.setOnClickListener {
            if (!checkStoragePermission()) {
                requestStoragePermissions()
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
                                AlertDialogs.createAccount(
                                    this@CreateArticleActivity,
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

        binding.removeImg.setOnClickListener {
            imageUri = null
            imagePart = null
            binding.articleImage.setImageURI(null)
            binding.removeImg.isVisible = false
            binding.articleImage.isVisible = false
            binding.addImageBtn.text = "إضافة صورة"
        }
    }

    private fun createArticle(description: String) {
        val articleService = ArticleService()
        articleService.create(
            this@CreateArticleActivity,
            currentUser.token,
            fcmToken,
            description,
            imagePart,
            isVerified
        )
        setArticleEmpty()
    }

    private fun getUserProfile() {
        val retrofitInstance = userApi.userProfile(currentUser.token)
        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(
                call: Call<Profile>,
                response: Response<Profile>
            ) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        saveProfile(
                            this@CreateArticleActivity,
                            profile.id!!,
                            profile.name,
                            profile.verified
                        )
                        loadProfileImage(
                            this@CreateArticleActivity,
                            profile.url,
                            binding.avatarImage
                        )
                        isVerified = profile.verified
                        binding.userName.apply {
                            text = profile.name
                            if (profile.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tooltipText = context.getString(R.string.verified)
                            } else {
                                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                            }
                            visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<Profile>,
                t: Throwable
            ) {
                binding.userName.apply {
                    text = currentProfile.name
                    if (currentProfile.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tooltipText = context.getString(R.string.verified)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                    visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setArticleEmpty() {
        binding.articleEdt.text!!.clear()
        binding.articleImage.setImageURI(null)
        onBackPressedDispatcher.onBackPressed()
    }
}