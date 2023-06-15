package com.aghourservices.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Article
import com.aghourservices.data.network.RetrofitInstance.articlesApi
import com.aghourservices.ui.adapters.DraftArticlesAdapter
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.services.cache.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DraftArticlesViewModel : ViewModel() {
    var newsLiveData = MutableLiveData<ArrayList<Article>>()
    var newsList: ArrayList<Article> = ArrayList()

    fun draftArticles(context: Context, userToken: String, fcmToken: String) {
        val retrofitBuilder = articlesApi.draftArticles(userToken, fcmToken)

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
                AlertDialogs.noInternet(context)
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

        val retrofitBuilder = articlesApi.deleteArticle(
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
                AlertDialogs.noInternet(context)
            }
        })
    }
}