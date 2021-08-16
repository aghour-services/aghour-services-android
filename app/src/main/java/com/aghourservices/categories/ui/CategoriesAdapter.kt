package com.aghourservices.categories.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.api.CategoryItem
import com.squareup.picasso.Picasso

class CategoriesAdapter(
    private val List: List<CategoryItem>,
    private val onItemClicked: (position: Int) -> Unit
) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_design, parent, false)
        return CategoryViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryItem = List[position]
        Picasso.get().load(categoryItem.url).into(holder.imageView)
        holder.textView.text = categoryItem.id.toString()
        holder.titleView.text = categoryItem.title
    }

    override fun getItemCount(): Int {
        return List.size
    }
}