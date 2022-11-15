package com.aghourservices.utils.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import androidx.core.view.isVisible
import com.aghourservices.R
import com.aghourservices.databinding.ThanksForSupportBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

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

    fun showThanksDialog(context: Activity) {
        val binding = ThanksForSupportBinding.inflate(context.layoutInflater)
        val bottomSheet = BottomSheetDialog(context).apply {
            setContentView(binding.root)
            setCancelable(true)
            show()
        }

        binding.closeToast.setOnClickListener {
            bottomSheet.dismiss()
        }
    }
}