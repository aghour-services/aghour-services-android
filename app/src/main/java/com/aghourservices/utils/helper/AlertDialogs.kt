package com.aghourservices.utils.helper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.aghourservices.R
import com.aghourservices.ui.activities.SignUpActivity

class AlertDialogs {

    companion object {
        fun errorLogin(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            alertDialogBuilder.setTitle(R.string.error_logIn)
            alertDialogBuilder.setIcon(R.mipmap.cloud)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
        }

        fun noInternet(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            alertDialogBuilder.setTitle(R.string.no_internet)
            alertDialogBuilder.setIcon(R.mipmap.cloud)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
        }

        fun dataAdded(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            alertDialogBuilder.setTitle(R.string.data_added)
            alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ ->
                val c = context as FragmentActivity
                c.onBackPressedDispatcher.onBackPressed()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
        }

        fun createAccount(context: Context, message: String) {
            val alertDialogBuilder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            alertDialogBuilder.setTitle(context.getString(R.string.create_account_first))
            alertDialogBuilder.setMessage(message)
            alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton("إنشاء الان") { _, _ ->
                context.startActivity(Intent(context, SignUpActivity::class.java))
                (context as AppCompatActivity).finishAffinity()
            }
            alertDialogBuilder.setNegativeButton(R.string.cancelButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 14f
        }
    }
}