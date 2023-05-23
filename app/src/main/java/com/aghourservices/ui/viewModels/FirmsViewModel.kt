package com.aghourservices.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Firm
import com.aghourservices.data.network.RetrofitInstance.firmsApi
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirmsViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()
    var firmsLiveData = MutableLiveData<ArrayList<Firm>>()
    var firmsList: ArrayList<Firm> = ArrayList()

    fun loadFirms(categoryId: Int, tagsAsParameter: String, fcmToken: String) {
        val retrofitBuilder = firmsApi.loadFirms(categoryId, tagsAsParameter, fcmToken)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Firm>?> {
            override fun onResponse(
                call: Call<ArrayList<Firm>?>,
                response: Response<ArrayList<Firm>?>,
            ) {
                if (response.isSuccessful) {
                    firmsLiveData.value = response.body()
                    firmsList = firmsLiveData.value!!

                    realm.executeTransaction {
                        firmsList.forEach {
                            val firm = realm.where(Firm::class.java).equalTo("id", it.id).findFirst()
                            if (firm != null) {
                                it.isFavorite = firm.isFavorite
                            }
                            realm.createOrUpdateObjectFromJson(Firm::class.java, it.toJSONObject())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Firm>?>, t: Throwable) {
                val result = realm.where(Firm::class.java).equalTo("category_id", categoryId).findAll()
                firmsList = ArrayList()
                firmsList.addAll(result)
                firmsLiveData.value = firmsList
            }
        })
    }
}