package com.aghourservices.settings

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.about.AboutFragment
import com.aghourservices.cache.UserInfo
import com.aghourservices.databinding.FragmentSettingBinding
import com.aghourservices.user.SignUpActivity

class SettingFragment : BaseFragment() {
    private lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        hideUserLogOut()
        hideBottomNav()

        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.hide()

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.darkMode.setOnClickListener {
            chooseThemeDialog()
        }
        binding.facebook.setOnClickListener {
            facebook()
        }
        binding.share.setOnClickListener {
            shareApp()
        }
        binding.rate.setOnClickListener {
            rateApp()
        }
        binding.about.setOnClickListener {
            loadFragments(AboutFragment(), true)
        }
        binding.logOut.setOnClickListener {
            showOnCloseDialog(requireActivity())
        }
    }

    private fun checkUser() {
        val userInfo = UserInfo()
        val user = userInfo.getUserData(requireActivity())

        if (userInfo.isUserLoggedIn(requireActivity())) {
            binding.btnRegister.visibility = View.GONE
            binding.userDataView.visibility = View.VISIBLE
            binding.userName.text = user.name
            binding.userMobile.text = user.mobile
            binding.userEmail.text = user.email
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(requireActivity(), SignUpActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun hideUserLogOut() {
        val isUserLogin = UserInfo().isUserLoggedIn(requireActivity())
        if (isUserLogin) {
            binding.logOut.visibility = View.VISIBLE
        } else {
            binding.logOut.visibility = View.GONE
        }
    }

    private fun chooseThemeDialog() {
        val activity = (requireActivity() as AppCompatActivity)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.choose_theme_text))
        builder.setNegativeButton(R.string.cancelButton) { _, _ -> }
        val styles = arrayOf("متوقف", "قيد التشغيل", "استخدام النظام الإفتراضي")
        val checkedItem = ThemePreference(requireContext()).darkMode
        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    ThemePreference(requireContext()).darkMode = 0
                    activity.delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    ThemePreference(requireContext()).darkMode = 1
                    activity.delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    ThemePreference(requireContext()).darkMode = 2
                    activity.delegate.applyDayNight()
                    dialog.dismiss()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 18f
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextAppearance(R.style.SegoeTextBold)
        }
    }
}