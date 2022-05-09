package com.aghourservices.interfaces

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.aghourservices.R

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
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
        }

        fun noInternet(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(R.string.no_internet)
            alertDialogBuilder.setIcon(R.mipmap.cloud)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
        }

        fun dataAdded(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(R.string.data_added)
            alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
        }

        fun articleAdded(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(R.string.article_added)
            alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.doneButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 14f
        }
    }
}