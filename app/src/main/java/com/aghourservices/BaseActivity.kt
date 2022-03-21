package com.aghourservices

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.settings.ThemePreference
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    fun notify(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun loadFragments(fragment: Fragment?, stacked: Boolean) {
        val backStateName: String = fragment?.javaClass.toString()
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()

        if (fragment != null) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            ft.replace(R.id.parent_container, fragment)
            if (stacked) {
                ft.addToBackStack(backStateName)
            }
            ft.commit()
        }
    }

    fun onSNACK(view: View) {
        val snackbar = Snackbar.make(view, "لا يوجد إنترنت", Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.BLACK)
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 18f
        snackbar.show()
    }
}