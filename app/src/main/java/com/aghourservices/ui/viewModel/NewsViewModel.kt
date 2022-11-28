package com.aghourservices.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()
    var newsLiveData = MutableLiveData<ArrayList<Article>>()
    var newsList: ArrayList<Article> = ArrayList()

    fun loadArticles(context: Context) {
        val retrofitBuilder = RetrofitInstance(context).newsApi.loadArticles()

        retrofitBuilder.enqueue(object : Callback<ArrayList<Article>?> {
            override fun onResponse(
                call: Call<ArrayList<Article>?>,
                response: Response<ArrayList<Article>?>,
            ) {
                if (response.isSuccessful) {
                    newsLiveData.value = response.body()
                    newsList = newsLiveData.value!!
                    realm.executeTransaction {
                        newsList.forEach {
                            val article = realm.where(Article::class.java).equalTo("id", it.id).findFirst()
                            if (article != null) {
                                it.isFavorite = article.isFavorite
                            }
                            realm.createOrUpdateObjectFromJson(Article::class.java, it.toJSONObject())
                        }
                        realm.copyToRealmOrUpdate(newsList)
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