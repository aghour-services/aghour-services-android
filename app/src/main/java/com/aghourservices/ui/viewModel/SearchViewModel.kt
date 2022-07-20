package com.aghourservices.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Search
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.utils.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {
    var searchLiveData = MutableLiveData<ArrayList<Search>>()

    fun search(context: Context, text: String) {
        val eventName = "search_${text}"
        Event.sendFirebaseEvent(eventName, text)

        val retrofitBuilder = RetrofitInstance(context).searchApi.search(text)

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