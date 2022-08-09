package com.aghourservices.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Firm
import com.aghourservices.data.request.RetrofitInstance
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirmsViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()
    var firmsLiveData = MutableLiveData<ArrayList<Firm>>()
    var firmsList: ArrayList<Firm> = ArrayList()

    fun loadFirms(context: Context, categoryId: Int, tagsAsParameter: String) {
        val retrofitBuilder = RetrofitInstance(context).firmsApi.loadFirms(categoryId, tagsAsParameter)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Firm>?> {
            override fun onResponse(
                call: Call<ArrayList<Firm>?>,
                response: Response<ArrayList<Firm>?>,
            ) {
                if (response.isSuccessful) {
                    firmsLiveData.value = response.body()
                    firmsList = firmsLiveData.value!!

                    realm.executeTransaction {
                        realm.copyToRealmOrUpdate(firmsList)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Firm>?>, t: Throwable) {
                val result =
                    realm.where(Firm::class.java).equalTo("category_id", categoryId).findAll()
                firmsList = ArrayList()
                firmsList.addAll(result)
                firmsLiveData.value = firmsList
            }
        })
    }
}