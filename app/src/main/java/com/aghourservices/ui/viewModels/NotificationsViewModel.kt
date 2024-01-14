package com.aghourservices.ui.viewModels

import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Notification
import com.aghourservices.data.network.RetrofitInstance.notificationsApi
import com.aghourservices.databinding.FragmentNotificationsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsViewModel : ViewModel() {
    val notificationsLiveData = MutableLiveData<ArrayList<Notification>>()
    var notificationsList: ArrayList<Notification> = ArrayList()

    fun getNotifications(binding: FragmentNotificationsBinding, fcmToken: String, userToken: String) {
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
                    binding.apply {
                        progressBar.isVisible = false
                        notificationsRv.isVisible = true
                    }
                    notificationsLiveData.value = response.body()
                    notificationsList = notificationsLiveData.value!!
                }
            }

            override fun onFailure(call: Call<ArrayList<Notification>>, t: Throwable) {
                binding.apply {
                    progressBar.isVisible = false
                    noInternet.isVisible = true
                }
            }
        })
    }
}
