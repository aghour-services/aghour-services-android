package com.aghourservices.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.databinding.CategoryCardBinding
import com.aghourservices.ui.viewHolders.CategoriesViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class CategoriesAdapter(
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<CategoriesViewHolder>() {
    private var categoryList: ArrayList<Category> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val categoryCard =
            CategoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(categoryCard, onItemClicked)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val categoryItems = categoryList[position]

        holder.binding.apply {
            Glide.with(root.context)
                .load(categoryItems.icon)
                .placeholder(R.drawable.ic_download)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(categoryIcon)

            categoryTitleTv.text = categoryItems.name
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCategories(categories: ArrayList<Category>) {
        categoryList = categories
        notifyDataSetChanged()
    }

    fun getCategory(position: Int): Category {
        return categoryList[position]
    }
}

