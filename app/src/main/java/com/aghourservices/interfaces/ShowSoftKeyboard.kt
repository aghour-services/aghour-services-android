package com.aghourservices.interfaces

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

interface ShowSoftKeyboard {
    companion object {
        fun show(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view,0)
        }
    }
}
