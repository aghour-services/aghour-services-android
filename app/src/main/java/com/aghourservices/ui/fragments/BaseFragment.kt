package com.aghourservices.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.aghourservices.R
import com.aghourservices.utils.helper.Event
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {
    private lateinit var bottomViewContainer: ConstraintLayout
    lateinit var appCompactActivity: AppCompatActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
        appCompactActivity = activity as AppCompatActivity
        bottomViewContainer = activity?.findViewById(R.id.bottomViewContainer) as ConstraintLayout
        clearGlideMemory()
    }

    private fun clearGlideMemory() {
        Glide.get(requireContext()).clearMemory()
    }

    fun showToolbar() {
        appCompactActivity.supportActionBar?.apply { show() }
    }

    fun hideToolbar() {
        appCompactActivity.supportActionBar?.apply { hide() }
    }


    fun showBottomNavigation() {
        bottomViewContainer.isVisible = true
    }

    fun hideBottomNavigation() {
        bottomViewContainer.isVisible = false
    }

    fun onSNACK(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.BLACK)
        val textView =
            snackbarView.findViewById(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 18f
        snackbar.show()
    }
}