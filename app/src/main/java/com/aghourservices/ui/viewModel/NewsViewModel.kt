package com.aghourservices.ui.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.db.RealmConfiguration
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel : ViewModel() {
    var newsLiveData = MutableLiveData<ArrayList<Article>>()
    var newsList: ArrayList<Article> = ArrayList()

    fun loadArticles(context: Activity, categoryId: Int) {
        val realm = RealmConfiguration(context).realm
        val retrofitBuilder = RetrofitInstance(context).newsApi.loadArticles(categoryId)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Article>?> {
            override fun onResponse(
                call: Call<ArrayList<Article>?>,
                response: Response<ArrayList<Article>?>,
            ) {
                if (response.isSuccessful) {
                    newsLiveData.value = response.body()
                    newsList = newsLiveData.value!!
                    realm.executeTransaction {
                        for (i in newsList) {
                            try {
                                val article = realm.createObject(Article::class.java, i.id)
                                article.description = i.description
                                article.created_at = i.created_at
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Article>?>, t: Throwable) {
                val result = realm.where(Article::class.java).findAll()
                newsList = ArrayList()
                newsList.addAll(result)
                newsLiveData.value = newsList
            }
        })
    }
}