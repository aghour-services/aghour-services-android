package com.aghourservices.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Search
import com.aghourservices.data.network.RetrofitInstance.searchApi
import com.aghourservices.utils.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {
    var searchLiveData = MutableLiveData<ArrayList<Search>>()

    fun search(text: String, fcmToken: String) {
        val eventName = "search_${text}"
        Event.sendFirebaseEvent(eventName, text)

        val retrofitBuilder = searchApi.search(text, fcmToken)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Search>?> {
            override fun onResponse(
                call: Call<ArrayList<Search>?>,
                response: Response<ArrayList<Search>?>,
            ) {
                if (response.isSuccessful) {
                    searchLiveData.value = response.body()
                }
            }

            override fun onFailure(call: Call<ArrayList<Search>?>, t: Throwable) {
            }
        })
    }

}