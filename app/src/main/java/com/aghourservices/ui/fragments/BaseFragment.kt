package com.aghourservices.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.aghourservices.R
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.helper.HasBottomNavigation
import com.aghourservices.utils.helper.HasToolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment(), HasToolbar, HasBottomNavigation {
    private lateinit var bottomNavigation: ConstraintLayout
    private lateinit var appBarLayout: AppBarLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
        bottomNavigation = requireActivity().findViewById(R.id.bottomViewContainer)
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout)
    }

    fun onSNACK(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.BLACK)
        val textView =
            snackBarView.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 18f
        snackBar.show()
    }

    override fun showBottomNavigation() {
        bottomNavigation.visibility = View.VISIBLE
    }

    override fun hideBottomNavigation() {
        bottomNavigation.visibility = View.GONE
    }

    override fun showToolbar() {
        appBarLayout.visibility = View.VISIBLE
    }

    override fun hideToolbar() {
        appBarLayout.visibility = View.GONE
    }
}