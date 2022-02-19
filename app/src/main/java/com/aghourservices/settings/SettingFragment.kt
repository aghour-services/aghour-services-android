package com.aghourservices.settings

import android.content.Intent
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
        // Inflate the layout for this fragment
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

        binding.darkMode.setOnClickListener {
            chooseThemeDialog()
        }
    }

    private fun checkUser() {
        val userInfo = UserInfo()
        if (userInfo.isUserLoggedIn(requireActivity())) {
            binding.btnRegister.visibility = View.GONE
            binding.userDataView.visibility = View.VISIBLE
            val user = userInfo.getUserData(requireActivity())
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

    /**
    private fun appTheme(){
    if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
    requireActivity().setTheme(R.style.Theme_DarkApp)
    } else {
    requireActivity().setTheme(R.style.Theme_LightApp)
    }
    }
    private fun switchChecked(){
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
    binding.switchMode.isChecked = true
    }
    binding.switchMode.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked){
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }else{
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
    }
    } **/

    private fun chooseThemeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.choose_theme_text))
        builder.setNegativeButton(R.string.cancelButton) { _, _ -> }
        val styles = arrayOf("قيد التشغيل", "متوقف", "استخدام النظام الإفتراضي")
        val checkedItem = ThemePreference.AppTheme(requireContext()).darkMode
//        val activity = (activity as AppCompatActivity)

        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    ThemePreference.AppTheme(requireContext()).darkMode = 0
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    ThemePreference.AppTheme(requireContext()).darkMode = 1
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    ThemePreference.AppTheme(requireContext()).darkMode = 2
                    dialog.dismiss()
                }

            }
        }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 20.toFloat()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextAppearance(R.style.SegoeTextBold)
        }
    }
}