package com.aghourservices.categories

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.Category
import com.aghourservices.constants.RetrofitInstance
import com.aghourservices.offline.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesViewModel : ViewModel() {
    val categoriesLiveData = MutableLiveData<ArrayList<Category>>()
    var categoryList: ArrayList<Category> = ArrayList()

    fun loadCategories(context: Activity) {
        val realm = RealmConfiguration(context).realm

        val retrofitData = RetrofitInstance(context).retrofit.create(ApiServices::class.java)
        val retrofitBuilder = retrofitData.loadCategoriesList()

        retrofitBuilder.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                if (response.isSuccessful) {
                    categoriesLiveData.value = response.body()
                    categoryList = categoriesLiveData.value!!
                    realm.executeTransaction {
                        try {
                            for (item in categoryList) {
                                val category = realm.createObject(
                                    Category::class.java,
                                    item.id
                                )
                                category.name = item.name
                                category.icon = item.icon
                            }
                        } catch (e: Exception) {
                        }
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
}