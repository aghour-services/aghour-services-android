package com.aghourservices.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Comment
import com.aghourservices.data.request.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsViewModel : ViewModel() {
    val commentsLivewData = MutableLiveData<ArrayList<Comment>>()
    var commentList: ArrayList<Comment> = ArrayList()

    fun loadComments(context: Context, articleId: Int, fcmToken: String) {
        val retrofitBuilder =
            RetrofitInstance(context).commentsApi.loadComments(articleId, fcmToken)

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
                com.aghourservices.utils.interfaces.AlertDialog.noInternet(context)
            }
        })
    }
}