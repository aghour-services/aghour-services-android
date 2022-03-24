package com.aghourservices.interfaces

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.cache.UserInfo
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.user.SignInActivity

interface AlertDialog {

    companion object{
        fun errorLogin(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(R.string.error_logIn)
            alertDialogBuilder.setIcon(R.mipmap.cloud)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 20f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextAppearance(R.style.SegoeTextBold)
            }
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }

        fun noInternet(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(R.string.no_internet)
            alertDialogBuilder.setIcon(R.mipmap.cloud)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 20f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextAppearance(R.style.SegoeTextBold)
            }
        }

        fun dataAdded(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(R.string.data_added)
            alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 20f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextAppearance(R.style.SegoeTextBold)
            }
        }
    }
}