package com.aghourservices.categories.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import kotlinx.android.synthetic.main.recycler_view_design.view.*

class CategoryViewHolder(itemView: View, private val onItemClicked: (position: Int) -> Unit) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val imageView: ImageView = itemView.findViewById(R.id.imageview)
    var firstTxt: TextView
    init {
        itemView.setOnClickListener(this)
        firstTxt = itemView.firstTxt
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}
