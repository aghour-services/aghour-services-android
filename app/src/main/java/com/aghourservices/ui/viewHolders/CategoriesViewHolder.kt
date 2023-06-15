package com.aghourservices.ui.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.databinding.CategoryCardBinding

class CategoriesViewHolder(
    val binding: CategoryCardBinding,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}