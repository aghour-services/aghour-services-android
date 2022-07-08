package com.aghourservices.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.databinding.CategoryCardBinding
import com.squareup.picasso.Picasso

class CategoriesAdapter(
    private var categoryList: List<Category>,
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val categoryCard =
            CategoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(categoryCard, onItemClicked)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as CategoryViewHolder
        val categoryItems = categoryList[position]

        holder.binding.apply {
            Picasso.get().load(categoryItems.icon)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .into(categoryIcon)
            categoryTitleTv.text = categoryItems.name
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}

