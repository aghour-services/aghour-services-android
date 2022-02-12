package com.aghourservices.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.about.AboutFragment
import com.aghourservices.cache.UserInfo
import com.aghourservices.databinding.FragmentSettingBinding
import com.aghourservices.user.SignUpActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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

    override fun onResume() {
        super.onResume()
        hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUser()
        hideUserLogOut()
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
            showOnCloseDialog()
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
}