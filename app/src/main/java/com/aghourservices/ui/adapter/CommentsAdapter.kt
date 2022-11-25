package com.aghourservices.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding

class CommentsAdapter(
    val context: Context,
    private var commentsList: ArrayList<Comment>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = CommentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hold = holder as CommentsViewHolder
        val comment = commentsList[position]
        hold.setCommentView(comment)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }
}