package com.aghourservices.ui.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance.likeApi
import com.aghourservices.data.request.RetrofitInstance.newsApi
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.adapter.DraftArticlesAdapter
import com.aghourservices.ui.main.cache.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DraftArticlesViewModel : ViewModel() {
    var newsLiveData = MutableLiveData<ArrayList<Article>>()
    var newsList: ArrayList<Article> = ArrayList()

    fun draftArticles(context: Context, userToken: String, fcmToken: String) {
        val retrofitBuilder = newsApi.draftArticles(userToken, fcmToken)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Article>?> {
            override fun onResponse(
                call: Call<ArrayList<Article>?>,
                response: Response<ArrayList<Article>?>,
            ) {
                if (response.isSuccessful) {
                    newsLiveData.value = response.body()
                    newsList = newsLiveData.value!!
                }
            }

            override fun onFailure(call: Call<ArrayList<Article>?>, t: Throwable) {
                com.aghourservices.utils.interfaces.AlertDialog.noInternet(context)
            }
        })
    }

    fun deleteArticle(
        context: Context,
        userToken: String,
        draftArticlesAdapter: DraftArticlesAdapter,
        position: Int
    ) {
        val articleId = draftArticlesAdapter.getArticle(position).id

        val retrofitBuilder = newsApi.deleteArticle(
            articleId,
            userToken,
            UserInfo.getFCMToken(context)
        )

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    draftArticlesAdapter.deleteArticle(position)
                    Toast.makeText(context, "تم مسح الخبر", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                com.aghourservices.utils.interfaces.AlertDialog.noInternet(context)
            }
        })
    }
}