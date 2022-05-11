package com.aghourservices

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.aghourservices.firebase_analytics.Event
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {
    lateinit var bottomNavigationView: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Event.sendScreenName(this::class.simpleName.toString())
    }

    fun showBottomNav() {
        bottomNavigationView = requireActivity().findViewById(R.id.linearBottomNavVIew)
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideBottomNav() {
        bottomNavigationView = requireActivity().findViewById(R.id.linearBottomNavVIew)
        bottomNavigationView.visibility = View.GONE
    }


    fun onSNACK(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.BLACK)
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 18f
        snackbar.show()
    }
}