package com.aghourservices.utils.helper

import android.app.Dialog
import android.content.Context
import com.aghourservices.R

object ProgressDialog {
    private lateinit var progressDialog: Dialog

    fun showProgressDialog(context: Context) {
        progressDialog = Dialog(context)
        progressDialog.setContentView(R.layout.dialog_custom_progress)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.hide()
    }
}