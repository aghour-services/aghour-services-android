package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.databinding.CategoryCardBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class CategoriesAdapter(
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var categoryList: ArrayList<Category> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val categoryCard =
            CategoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(categoryCard, onItemClicked)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as CategoryViewHolder
        val categoryItems = categoryList[position]

        holder.binding.apply {
            Glide.with(root.context)
                .load(categoryItems.icon)
                .placeholder(R.drawable.ic_loading)
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

    inner class CategoryViewHolder(
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
}

