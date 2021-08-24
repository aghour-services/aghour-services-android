package com.aghourservices.markets.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import kotlinx.android.synthetic.main.markets_view.view.*

class MarketsViewHolder(
    itemView: View,
    private val onItemClicked: (position: Int) -> Unit
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val imageViewer: ImageView = itemView.findViewById(R.id.image)
    var txtOne: TextView = itemView.TxtOne

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}