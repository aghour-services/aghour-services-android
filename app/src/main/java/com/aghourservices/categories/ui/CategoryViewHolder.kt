package com.aghourservices.categories.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R

class CategoryViewHolder(
    itemView: View, private val onItemClicked:
        (position: Int) -> Unit
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val imageView: ImageView = itemView.findViewById(R.id.category_icon)
    var firstTxt: TextView = itemView.findViewById(R.id.category_title_tv)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}