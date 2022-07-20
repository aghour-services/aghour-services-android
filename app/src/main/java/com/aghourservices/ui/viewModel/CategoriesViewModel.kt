package com.aghourservices.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.db.RealmConfiguration
import com.aghourservices.data.model.Category
import com.aghourservices.data.request.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesViewModel : ViewModel() {
    val categoriesLiveData = MutableLiveData<ArrayList<Category>>()
    var categoryList: ArrayList<Category> = ArrayList()

    fun loadCategories(context: Context) {
        val realm = RealmConfiguration(context).realm
        val retrofitBuilder = RetrofitInstance(context).categoriesApi.loadCategoriesList()

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