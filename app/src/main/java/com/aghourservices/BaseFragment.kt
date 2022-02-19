package com.aghourservices

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.interfaces.ActivityFragmentCommunicator
import com.aghourservices.user.SignInActivity

open class BaseFragment : Fragment(), ActivityFragmentCommunicator {
    lateinit var bottomNavigationView: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    fun notify(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showBottomNav() {
        bottomNavigationView = requireActivity().findViewById(R.id.linearBottomNavVIew)
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideBottomNav() {
        bottomNavigationView = requireActivity().findViewById(R.id.linearBottomNavVIew)
        bottomNavigationView.visibility = View.GONE
    }

    fun loadFragments(fragment: Fragment?, stacked: Boolean) {
        val backStateName: String = fragment?.javaClass.toString()
        val manager: FragmentManager = requireActivity().supportFragmentManager
        if (fragment != null) {
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            ft.replace(R.id.parent_container, fragment)
            if (stacked) {
                ft.addToBackStack(backStateName)
            }
            ft.commit()
        }
    }

    fun shareApp() {
        Event.sendFirebaseEvent("Share", "")
        val shareText = getString(R.string.shareText)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "مشاركة التطبيق"))
    }

    fun rateApp() {
        Event.sendFirebaseEvent("Rate", "")
        val url = "https://play.google.com/store/apps/details?id=com.aghourservices"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun facebook() {
        Event.sendFirebaseEvent("Facebook_Page", "")
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb:/page/110004384736318")))
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebook.com/aghourservices")
                )
            )
        }
    }

    private fun logOut() {
        Event.sendFirebaseEvent("Sign_Out", "")
        UserInfo().clearUserData(requireActivity())
        startActivity(Intent(requireActivity(), SignInActivity::class.java))
        requireActivity().finish()
    }

    fun showOnCloseDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(R.string.title)
        alertDialogBuilder.setMessage(R.string.message)
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(R.string.positiveButton) { _, _ ->
            logOut()
            Event.sendFirebaseEvent("ALERT_LOGOUT_ACTION", "")
        }
        alertDialogBuilder.setNegativeButton(R.string.negativeButton) { _, _ ->
            Event.sendFirebaseEvent("ALERT_STAY_ACTION", "")
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)
    }
}