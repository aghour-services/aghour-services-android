package com.aghourservices

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.aghourservices.categories.CategoriesFragment
import com.aghourservices.settings.ThemePreference

open class BaseActivity : AppCompatActivity() {

    fun notify(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun loadFragments(fragment: Fragment?, stacked: Boolean) {
        val backStateName: String = fragment?.javaClass.toString()
        val manager: FragmentManager = supportFragmentManager
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

}