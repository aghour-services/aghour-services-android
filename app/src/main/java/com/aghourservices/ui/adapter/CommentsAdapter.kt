package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding
import com.aghourservices.ui.main.cache.UserInfo.isUserLoggedIn

class CommentsAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    private var commentsList: ArrayList<Comment> = ArrayList()

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

    fun getComment(position: Int): Comment {
        return commentsList[position]
    }

    inner class CommentsViewHolder(
        val binding: CommentCardBinding,
        private val onItemClicked: (v: View, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val isUserLoggedIn = isUserLoggedIn(binding.root.context)

        init {
            binding.updateComment.setOnClickListener(this)
            binding.deleteComment.setOnClickListener(this)
        }

        fun setCommentView(comment: Comment) {

            binding.apply {
                body.text = comment.body
                userName.text = comment.user?.name
            }

            if (!isUserLoggedIn) {
                binding.apply {
                    updateComment.visibility = View.GONE
                    deleteComment.visibility = View.GONE
                }
            }
        }

        override fun onClick(v: View?) {
            onItemClicked(v!!, absoluteAdapterPosition)
        }
    }
}