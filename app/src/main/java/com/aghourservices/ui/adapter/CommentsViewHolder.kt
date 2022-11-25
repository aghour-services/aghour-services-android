package com.aghourservices.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Article
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding
import com.aghourservices.databinding.NewsCardBinding
import com.aghourservices.utils.helper.Intents.copyNews
import com.aghourservices.utils.helper.Intents.shareNews

class CommentsViewHolder(
    val binding: CommentCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setCommentView(comment: Comment) {
        binding.apply {
            body.text = comment.body
            date.text = comment.created_at
        }
    }
}


