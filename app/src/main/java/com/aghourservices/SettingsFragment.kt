package com.aghourservices

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.about.AboutFragment
import com.aghourservices.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.setting_fragment)

        /** hide actionBar **/
        val activity = (activity as AppCompatActivity?)!!
        activity.supportActionBar!!.hide()

        binding.share.setOnClickListener {
            shareApp()
        }

        binding.rate.setOnClickListener {
            rateApp()
        }
        binding.about.setOnClickListener {
            replaceFragment(AboutFragment(), true)
        }
    }
}