package com.aghourservices

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.interfaces.ActivityFragmentCommunicator

open class BaseFragment : Fragment(), ActivityFragmentCommunicator {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
    }

    fun shareApp() {
        Event.sendFirebaseEvent("Share", "")
        val shareText = getString(R.string.shareText)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "مشاركة"))
    }

    fun rateApp() {
        Event.sendFirebaseEvent("Rate", "")
        val url = "https://play.google.com/store/apps/details?id=com.aghourservices"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun replaceFragment(fragment: Fragment, stacked: Boolean) {
        val backStateName: String = fragment.javaClass.toString()
        val manager: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        ft.replace(R.id.fragmentContainerView, fragment)
        if (stacked) {
            ft.addToBackStack(backStateName)
        }
        ft.commit()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}