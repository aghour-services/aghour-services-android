package com.aghourservices

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.aghourservices.ads.Interstitial
import com.aghourservices.firebase_analytics.Event

open class BaseFragment : Fragment(), ActivityFragmentCommunicator {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}