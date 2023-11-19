package com.aghourservices.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Notification
import com.aghourservices.data.network.RetrofitInstance.notificationsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsViewModel : ViewModel() {
    val notificationsLiveData = MutableLiveData<ArrayList<Notification>>()
    var notificationsList: ArrayList<Notification> = ArrayList()

    fun getNotifications(context: Context, fcmToken: String, userToken: String) {
        val retrofitBuilder = notificationsApi.getNotifications(
            fcmToken,
            userToken
        )

        retrofitBuilder.enqueue(object : Callback<ArrayList<Notification>> {
            override fun onResponse(
                call: Call<ArrayList<Notification>>,
                response: Response<ArrayList<Notification>>
            ) {
                if (response.isSuccessful) {
                    notificationsLiveData.value = response.body()
                    notificationsList = notificationsLiveData.value!!
                }
            }

            override fun onFailure(call: Call<ArrayList<Notification>>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
