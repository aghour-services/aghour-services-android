package com.aghourservices.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.google.android.gms.ads.AdView

class AboutFragment : Fragment() {

    lateinit var adView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.about_fragment)

        adView = requireActivity().findViewById(R.id.adView)
        Banner.show(requireActivity(), adView)
    }
}