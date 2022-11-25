package com.aghourservices.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Category
import com.aghourservices.data.model.Comment
import com.aghourservices.data.request.RetrofitInstance
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsViewModel : ViewModel() {
    private var realm: Realm = Realm.getDefaultInstance()
    val commentsLivewData = MutableLiveData<ArrayList<Comment>>()
    var commentList: ArrayList<Comment> = ArrayList()

    fun loadComments(context: Context, articleId: Int) {
        val retrofitBuilder = RetrofitInstance(context).commentsApi.loadComments(articleId)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Comment>> {
            override fun onResponse(
                call: Call<ArrayList<Comment>>, response: Response<ArrayList<Comment>>
            ) {
                if (response.isSuccessful) {
                    commentsLivewData.value = response.body()
                    commentList = commentsLivewData.value!!
                }
            }

            override fun onFailure(call: Call<ArrayList<Comment>>, t: Throwable) {
                Log.d("list-comment", "onFailure: ${t.message}")
            }
        })
    }
}