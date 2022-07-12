package com.aghourservices.ui.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.db.RealmConfiguration
import com.aghourservices.data.model.Firm
import com.aghourservices.data.model.Tag
import com.aghourservices.data.request.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirmsViewModel : ViewModel() {
    var firmsLiveData = MutableLiveData<ArrayList<Firm>>()
    var firmsList: ArrayList<Firm> = ArrayList()

    var tagsLiveData = MutableLiveData<ArrayList<Tag>>()
    var tagsList: ArrayList<Tag> = ArrayList()

    fun loadFirms(context: Activity, categoryId: Int) {
        val realm = RealmConfiguration(context).realm
        val retrofitBuilder = RetrofitInstance(context).firmsApi.loadFirms(categoryId)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Firm>?> {
            override fun onResponse(
                call: Call<ArrayList<Firm>?>,
                response: Response<ArrayList<Firm>?>,
            ) {
                if (response.isSuccessful) {
                    firmsLiveData.value = response.body()
                    firmsList = firmsLiveData.value!!

                    realm.executeTransaction {
                        for (i in firmsList) {
                            try {
                                val firm =
                                    realm.where(Firm::class.java).equalTo("id", i.id).findFirst()
                                if (firm != null) {
                                    i.isFavorite = firm.isFavorite
                                }
                                realm.createOrUpdateObjectFromJson(
                                    Firm::class.java,
                                    i.toJSONObject()
                                )
                            } catch (e: Exception) {
                            }
                        }
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

    fun loadTags(context: Activity, categoryId: Int) {
        val retrofitBuilder = RetrofitInstance(context).tagsApi.loadTags(categoryId)
        retrofitBuilder.enqueue(object : Callback<ArrayList<Tag>?> {
            override fun onResponse(
                call: Call<ArrayList<Tag>?>,
                response: Response<ArrayList<Tag>?>,
            ) {
                if (response.isSuccessful) {
                    tagsLiveData.value = response.body()
                    tagsList = tagsLiveData.value!!
                }
            }

            override fun onFailure(call: Call<ArrayList<Tag>?>, t: Throwable) {}
        })
    }
}