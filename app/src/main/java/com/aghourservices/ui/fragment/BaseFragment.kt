package com.aghourservices.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.aghourservices.R
import com.aghourservices.utils.helper.Event
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fabButton: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
        bottomNavigationView = activity?.findViewById(R.id.bottomView) as BottomNavigationView
        fabButton = activity?.findViewById(R.id.floatingActionButton) as FloatingActionButton

    }

    fun showToolbar() {
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.show()
    }

    fun hideToolbar() {
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.hide()
    }

    fun showBottomNavigation() {
        bottomNavigationView.isVisible = true
        fabButton.show()
    }

    fun hideBottomNavigation() {
        bottomNavigationView.isVisible = false
        fabButton.hide()
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