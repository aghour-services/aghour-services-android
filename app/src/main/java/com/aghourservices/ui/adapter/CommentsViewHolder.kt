package com.aghourservices.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding

class CommentsViewHolder(
    val binding: CommentCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setCommentView(comment: Comment) {
        binding.apply {
            body.text = comment.body
            userName.text = comment.user?.name
        }
    }
}


