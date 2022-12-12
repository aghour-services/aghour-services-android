package com.aghourservices.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Category
import com.aghourservices.data.model.Device
import com.aghourservices.data.request.RetrofitInstance
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()
    val categoriesLiveData = MutableLiveData<ArrayList<Category>>()
    var categoryList: ArrayList<Category> = ArrayList()
    val deviceData = MutableLiveData<Device>()

    fun loadCategories(context: Context, deviceId: String) {
        val retrofitBuilder = RetrofitInstance(context).categoriesApi.loadCategoriesList(deviceId)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                if (response.isSuccessful) {
                    categoriesLiveData.value = response.body()
                    categoryList = categoriesLiveData.value!!
                    realm.executeTransaction { realm ->
                        realm.copyToRealmOrUpdate(categoryList)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                val categories = realm.where(Category::class.java).findAll()
                categoryList = ArrayList()
                categoryList.addAll(categories)
                categoriesLiveData.value = categoryList
            }
        })
    }

    fun sendDevice(context: Context, device: Device, deviceId: String) {
        val retrofitBuilder = RetrofitInstance(context).userApi.sendDevice(device, deviceId)
        retrofitBuilder.enqueue(object : Callback<Device> {
            override fun onResponse(call: Call<Device>, response: Response<Device>) {
                if (response.isSuccessful) {
                    deviceData.postValue(response.body())
                    Log.d("response", "onResponse: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<Device>, t: Throwable) {
                Log.d("response", "onFailure: ${t.message}")
            }
        })
    }
}
