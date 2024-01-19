package com.aghourservices.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghourservices.data.model.Comment
import com.aghourservices.data.network.RetrofitInstance.commentsApi
import com.aghourservices.ui.adapters.CommentsAdapter
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsViewModel : ViewModel() {
    val commentsLiveData = MutableLiveData<ArrayList<Comment>>()
    var commentList: ArrayList<Comment> = ArrayList()

    val addCommentLiveData = MutableLiveData<Comment>()
    var addCommentList: Comment = Comment()

    fun loadComments(context: Context, articleId: Int, fcmToken: String) {
        val retrofitBuilder = commentsApi.loadComments(articleId, fcmToken)

        retrofitBuilder.enqueue(object : Callback<ArrayList<Comment>> {
            override fun onResponse(
                call: Call<ArrayList<Comment>>, response: Response<ArrayList<Comment>>
            ) {
                if (response.isSuccessful) {
                    commentsLiveData.value = response.body()
                    commentList = commentsLiveData.value!!
                }
            }

            override fun onFailure(call: Call<ArrayList<Comment>>, t: Throwable) {}
        })
    }

    fun addComment(
        context: Context,
        articleId: Int,
        userToken: String,
        commentsAdapter: CommentsAdapter,
        comment: Comment,
        fcmToken: String
    ) {
        val retrofitBuilder = commentsApi.postComment(
            articleId,
            userToken,
            comment.toJsonObject(),
            fcmToken
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    val eventName = "Comment_Added"
                    addCommentLiveData.value = response.body()
                    addCommentList = addCommentLiveData.value!!
                    commentsAdapter.addComment(addCommentList)
                    Event.sendFirebaseEvent(eventName, "")
                    Toast.makeText(context, "تم اضافة التعليق", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                AlertDialogs.noInternet(context)
            }
        })
    }

    fun deleteComment(
        context: Context,
        articleId: Int,
        commentId: Int,
        userToken: String,
        position: Int,
        commentsAdapter: CommentsAdapter,
        fcmToken: String
    ) {

        val retrofitBuilder = commentsApi.deleteComment(
            articleId,
            commentId,
            userToken,
            fcmToken
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    commentsAdapter.removeComment(position)
                    Toast.makeText(context, "تم مسح التعليق", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                AlertDialogs.noInternet(context)
            }
        })
    }
}