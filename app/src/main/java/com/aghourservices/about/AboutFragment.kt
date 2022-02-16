package com.aghourservices.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.ads.Interstitial
import com.aghourservices.databinding.FragmentAboutBinding
import com.aghourservices.databinding.FragmentSearchBinding
import com.google.android.gms.ads.AdView

class AboutFragment : BaseFragment() {
    lateinit var binding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        hideBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.hide()

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}