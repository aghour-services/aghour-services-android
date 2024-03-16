package com.aghourservices.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance.userApi
import com.aghourservices.databinding.BottomSheetEditProfileBinding
import com.aghourservices.databinding.FragmentCurrentUserBinding
import com.aghourservices.ui.activities.SignUpActivity
import com.aghourservices.ui.base.BaseFragment
import com.aghourservices.utils.helper.Constants
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.helper.Intents
import com.aghourservices.utils.helper.Intents.loadProfileImage
import com.aghourservices.utils.helper.Intents.rateApp
import com.aghourservices.utils.helper.Intents.shareApp
import com.aghourservices.utils.helper.Intents.showOnCloseDialog
import com.aghourservices.utils.helper.ThemePreference
import com.aghourservices.utils.services.UserService
import com.aghourservices.utils.services.cache.UserInfo.saveProfile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CurrentUserFragment : BaseFragment() {
    private var _binding: FragmentCurrentUserBinding? = null
    private val binding get() = _binding!!
    private var avatarUri: Uri? = null
    private var avatarPart: MultipartBody.Part? = null
    private var currentUserAvatar: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentUserBinding.inflate(layoutInflater)
        checkUser()
        initUserClick()
        getProfile()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLogin) {
            requireActivity().title = currentUser.name
        } else {
            requireActivity().title = getString(R.string.setting_fragment)
        }
        showBottomNavigation()
        showToolbar()

        if (!checkStoragePermission()) {
            requestStoragePermissions()
        }
    }

    private fun initUserClick() {
        binding.apply {
            appTheme.setOnClickListener {
                chooseThemeDialog()
            }
            share.setOnClickListener {
                shareApp(requireContext())
            }
            rate.setOnClickListener {
                rateApp(requireContext())
            }
            logOut.setOnClickListener {
                showOnCloseDialog(requireContext())
            }
        }

        binding.apply {
            facebook.setOnClickListener {
                Intents.facebook(requireContext())
            }
            email.setOnClickListener {
                Intents.gmail(requireContext())
            }
            whatsApp.setOnClickListener {
                Intents.whatsApp(requireContext(), getString(R.string.whats_app_number))
            }
        }
    }

    private fun checkUser() {
        if (isUserLogin) {
            binding.apply {
                userLayout.visibility = View.VISIBLE
                logOut.visibility = View.VISIBLE
                editProfile.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                stopShimmer()
                createAccountLayout.visibility = View.VISIBLE
                userLayout.visibility = View.INVISIBLE
                logOut.visibility = View.GONE
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(requireContext(), SignUpActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    private fun profileUserClicks() {
        binding.apply {
            avatarImage.setOnClickListener {
                fullScreenAvatar(currentUserAvatar, binding.userName.text.toString(), avatarImage)
            }
            addUserImage.setOnClickListener {
                if (!checkStoragePermission()) {
                    requestStoragePermissions()
                } else {
                    openGallery()
                }
            }
            binding.editProfile.setOnClickListener { bottomSheetEditProfile() }
        }
    }


    private fun bottomSheetEditProfile() {
        val binding = BottomSheetEditProfileBinding.inflate(layoutInflater)

        val bottomSheet = BottomSheetDialog(requireContext()).apply {
            setContentView(binding.root)
            setCancelable(true)
            show()
        }

        binding.apply {
            profilePicture.setOnClickListener {
                if (!checkStoragePermission()) {
                    requestStoragePermissions()
                } else {
                    openGallery()
                    bottomSheet.dismiss()
                }
            }
        }

        binding.apply {
            userName.setOnClickListener {
                showNamePhoneUpdateDialog("name")
                bottomSheet.dismiss()
            }
            userPhone.setOnClickListener {
                showNamePhoneUpdateDialog("phone")
                bottomSheet.dismiss()
            }
        }

        binding.closeSheetBtn.setOnClickListener { bottomSheet.dismiss() }
    }

    @SuppressLint("SetTextI18n")
    private fun showNamePhoneUpdateDialog(key: String) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_user_details, null);
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val editLayout = dialogView.findViewById(R.id.user_layout) as TextInputLayout
        val nameOrPhone = dialogView.findViewById(R.id.user_info_edt) as TextInputEditText
        val headerName = dialogView.findViewById(R.id.edit_name) as TextView
        val updateBtn = dialogView.findViewById(R.id.update_btn) as Button
        val cancelBtn = dialogView.findViewById(R.id.cancel_btn) as Button
        val userName = binding.userName.text.toString().trim()
        val userPhone = binding.userPhone.text.toString().trim()
        when (key) {
            "name" -> nameOrPhone.setText(userName)
            "phone" -> nameOrPhone.setText(userPhone)
        }
        headerName.text = "تعديل الأسم"
        nameOrPhone.hint = "تعديل رقم الهاتف"
        val dialog = builder.create()
        dialog.show()

        cancelBtn.setOnClickListener { dialog.dismiss() }
        updateBtn.setOnClickListener {
            val value = nameOrPhone.text.toString().trim()
            if (value.isEmpty()) {
                editLayout.error = "الرجاء إدخال $key"
            } else {
                dialog.dismiss()
            }
        }
    }

    private fun getProfile() {
        val retrofitInstance = userApi.userProfile(currentUser.token)

        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                val profile = response.body()

                if (response.isSuccessful) {
                    stopShimmer()
                    if (profile != null) {
                        context?.let {
                            saveProfile(
                                it,
                                profile.id!!,
                                profile.name,
                                profile.verified
                            )
                        }
                        context?.let {
                            loadProfileImage(
                                it,
                                profile.url,
                                binding.avatarImage
                            )
                        }
                        currentUserAvatar = profile.url
                        binding.userName.apply {
                            text = profile.name
                            if (profile.verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tooltipText = context.getString(R.string.verified)
                            } else {
                                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                            }
                            visibility = View.VISIBLE
                        }
                        binding.apply {
                            userEmail.text = profile.email
                            userPhone.text = profile.mobile
                        }
                        profileUserClicks()
                    }
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                stopShimmer()
                binding.apply {
                    userEmail.text = currentUser.email
                    userPhone.text = currentUser.mobile
                }
                binding.userName.apply {
                    text = currentProfile?.name
                    if (currentProfile?.verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tooltipText = context.getString(R.string.verified)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                    visibility = View.VISIBLE
                }
                profileUserClicks()
                binding.avatarImage.setImageResource(R.mipmap.user)
            }
        })
    }

    private fun chooseThemeDialog() {
        Event.sendFirebaseEvent("App_Theme", "")
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        builder.setTitle(getString(R.string.choose_theme_text))
        builder.setNegativeButton(R.string.cancelButton) { _, _ -> }
        val styles = arrayOf(
            getString(R.string.defaultTheme), getString(R.string.light), getString(
                R.string.dark
            )
        )
        val checkedItem = ThemePreference(requireContext()).darkMode
        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    ThemePreference(requireContext()).darkMode = 0
                    dialog.dismiss()
                }

                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    ThemePreference(requireContext()).darkMode = 1
                    dialog.dismiss()
                }

                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    ThemePreference(requireContext()).darkMode = 2
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    private fun stopShimmer() {
        binding.apply {
            profileShimmer.stopShimmer()
            profileShimmer.visibility = View.INVISIBLE
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            avatarUri = data?.data!!
            lifecycleScope.launch {
                val file = File(Intents.getRealPathFromURI(requireContext(), avatarUri!!)!!)
                val compressedImage = Intents.compressImage(requireContext(), file.path)
                val requestBody = compressedImage.asRequestBody("image/*".toMediaTypeOrNull())
                avatarPart =
                    MultipartBody.Part.createFormData(
                        "user[avatar]",
                        compressedImage.name,
                        requestBody
                    )
                binding.avatarImage.setImageURI(avatarUri)
                updateProfileAvatar()
            }
        }
    }

    private fun updateProfileAvatar() {
        val userService = UserService()
        userService.updateAvatar(
            requireContext(),
            currentUser.token,
            avatarPart,
        )
    }
}