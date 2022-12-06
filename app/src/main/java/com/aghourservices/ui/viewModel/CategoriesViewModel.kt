package com.aghourservices.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Category
import com.aghourservices.data.request.RetrofitInstance
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()
    val categoriesLiveData = MutableLiveData<ArrayList<Category>>()
    var categoryList: ArrayList<Category> = ArrayList()

    fun loadCategories(context: Context) {
        val retrofitBuilder = RetrofitInstance(context).categoriesApi.loadCategoriesList()

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
}
