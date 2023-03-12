package com.aghourservices.ui.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.main.cache.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel : ViewModel() {
    var newsLiveData = MutableLiveData<ArrayList<Article>>()
    var newsList: ArrayList<Article> = ArrayList()

    fun loadArticles(context: Context, userToken: String, fcmToken: String) {
        val retrofitBuilder = RetrofitInstance(context).newsApi.loadArticles(userToken, fcmToken)

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

            override fun onFailure(call: Call<ArrayList<Article>?>, t: Throwable) {}
        })
    }

    fun deleteArticle(
        context: Context,
        userToken: String,
        articlesAdapter: ArticlesAdapter,
        position: Int
    ) {
        val articleId = articlesAdapter.getArticle(position).id

        val retrofitBuilder = RetrofitInstance(context).newsApi.deleteArticle(
            articleId,
            userToken,
            UserInfo.getFCMToken(context)
        )

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    articlesAdapter.deleteArticle(position)
                    Toast.makeText(context, "تم مسح الخبر", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                com.aghourservices.utils.interfaces.AlertDialog.noInternet(context)
            }
        })
    }

    fun likeArticle(
        context: Context,
        userToken: String,
        articlesAdapter: ArticlesAdapter,
        position: Int
    ) {
        val article = articlesAdapter.getArticle(position)
        article.liked = true

        val retrofitInstance = RetrofitInstance(context)
        val likeApi = retrofitInstance.likeApi
        val request = likeApi.likeArticle(article.id, userToken)

        request.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    val updatedArticle = response.body()
                    if (updatedArticle != null) {
                        articlesAdapter.updateArticle(position, updatedArticle)
                    }
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                Log.e("LIKE", "onFailure: ${t.message}")
            }
        })
    }

    fun unLikeArticle(
        context: Context,
        userToken: String,
        articlesAdapter: ArticlesAdapter,
        position: Int
    ) {
        val article = articlesAdapter.getArticle(position)
        article.liked = false

        val retrofitInstance = RetrofitInstance(context)
        val likeApi = retrofitInstance.likeApi
        val request = likeApi.unLikeArticle(article.id, userToken)

        request.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    val updatedArticle = response.body()
                    if (updatedArticle != null) {
                        articlesAdapter.updateArticle(position, updatedArticle)
                    }
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                Log.e("LIKE", "onFailure: ${t.message}")
            }
        })
    }
}