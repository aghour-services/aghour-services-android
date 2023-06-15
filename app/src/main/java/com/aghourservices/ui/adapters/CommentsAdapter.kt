package com.aghourservices.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding
import com.aghourservices.ui.viewHolders.CommentsViewHolder

class CommentsAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<CommentsViewHolder>() {
    var commentsList: ArrayList<Comment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = CommentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CommentsViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val comment = commentsList[position]
        holder.setCommentView(comment)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setComments(comments: ArrayList<Comment>) {
        commentsList = comments
        notifyDataSetChanged()
    }

    fun addComment(comment: Comment) {
        commentsList.add(comment)
        notifyItemInserted(commentsList.size - 1)
    }

    fun removeComment(position: Int) {
        commentsList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getComment(position: Int): Comment {
        return commentsList[position]
    }
}