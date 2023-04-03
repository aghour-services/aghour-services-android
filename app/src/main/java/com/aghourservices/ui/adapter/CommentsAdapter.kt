package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding
import com.aghourservices.ui.main.cache.UserInfo.getProfile

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
            binding.popupMenu.setOnClickListener(this)
        }

        fun setCommentView(comment: Comment) {
            val profile = getProfile(binding.root.context)

            binding.apply {
                body.text = comment.body
                userName.text = comment.user?.name
                time.text = comment.created_at
            }

            binding.userName.apply {
                if (comment.user?.is_verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tooltipText = context.getString(R.string.verified)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }

            if (comment.user?.id == profile.id) {
                binding.popupMenu.isVisible = true
            }
        }

        override fun onClick(v: View?) {
            onItemClicked(v!!, absoluteAdapterPosition)
        }
    }
}