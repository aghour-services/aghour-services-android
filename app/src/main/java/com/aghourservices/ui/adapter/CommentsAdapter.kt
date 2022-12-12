package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding
import com.aghourservices.ui.main.cache.UserInfo.getUserID

class CommentsAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
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

    inner class CommentsViewHolder(
        val binding: CommentCardBinding,
        private val onItemClicked: (v: View, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.updateComment.setOnClickListener(this)
            binding.deleteComment.setOnClickListener(this)
        }

        fun setCommentView(comment: Comment) {
            val profile = getUserID(binding.root.context)

            binding.apply {
                body.text = comment.body
                userName.text = comment.user?.name
                time.text = comment.created_at
            }

            if (comment.user?.id != profile.id) {
                binding.apply {
                    updateComment.visibility = View.GONE
                    deleteComment.visibility = View.GONE
                }
            }

            Log.d("profile", profile.id.toString())
        }

        override fun onClick(v: View?) {
            onItemClicked(v!!, absoluteAdapterPosition)
        }
    }
}