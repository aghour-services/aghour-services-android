package com.aghourservices.news.ui

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R

class ArticlesViewHolder(
    itemView: View,
) :
    RecyclerView.ViewHolder(itemView) {
//    var adFrame: ViewGroup = itemView.findViewById(R.id.ad_frame)
    var description: TextView = itemView.findViewById(R.id.description)
    var date: TextView = itemView.findViewById(R.id.date)
}