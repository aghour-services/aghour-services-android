package com.aghourservices.categories.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R

class CategoryViewHolder(itemView: View, private val onItemClicked: (position: Int) -> Unit) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val imageView: ImageView = itemView.findViewById(R.id.imageview)
    val textView: TextView = itemView.findViewById(R.id.firstTxt)
    val titleView: TextView = itemView.findViewById(R.id.secondTxt)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}
