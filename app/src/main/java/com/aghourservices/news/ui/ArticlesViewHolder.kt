package com.aghourservices.news.ui

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R

class ArticlesViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val adFrame: ViewGroup = itemView.findViewById(R.id.ad_frame)
    val description: TextView = itemView.findViewById(R.id.description)
    val date: TextView = itemView.findViewById(R.id.date)
    private val shareButton: TextView = itemView.findViewById(R.id.shareNews)

    init {
        shareButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        onItemClicked(absoluteAdapterPosition)
    }
}