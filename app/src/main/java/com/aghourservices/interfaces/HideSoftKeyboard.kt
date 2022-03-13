package com.aghourservices.interfaces

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

interface HideSoftKeyboard {
    companion object {
        fun hide(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}