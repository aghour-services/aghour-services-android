package com.aghourservices.markets.ui

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R

class MarketsViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var imageViewer: ImageView = itemView.findViewById(R.id.image)
    var id: TextView = itemView.findViewById(R.id.id)
    var title: TextView = itemView.findViewById(R.id.title)
    var imageButton: ImageButton = itemView.findViewById(R.id.btnCall)

    init {
        imageButton.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}