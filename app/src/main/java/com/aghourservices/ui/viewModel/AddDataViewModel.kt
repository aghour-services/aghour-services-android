package com.aghourservices.ui.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Firm
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.showProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDataViewModel : ViewModel() {

    fun createFirm(context: Activity, firm: Firm) {
        showProgressDialog(context)
        val user = UserInfo.getUserData(context)
        val retrofitBuilder =
            RetrofitInstance(context).firmsApi.createFirm(firm.toJsonObject(), user.token)

        retrofitBuilder.enqueue(object : Callback<Firm> {
            override fun onResponse(call: Call<Firm>, response: Response<Firm>) {
                if (response.isSuccessful) {
                    AlertDialog.dataAdded(context)
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<Firm>, t: Throwable) {
                AlertDialog.noInternet(context)
                hideProgressDialog()
            }
        })
    }
}