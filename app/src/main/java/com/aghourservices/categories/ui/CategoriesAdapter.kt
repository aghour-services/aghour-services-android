package com.aghourservices.categories.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.categories.api.Category
import com.squareup.picasso.Picasso

class CategoriesAdapter(
    private val List: List<Category>,
    private val onItemClicked: (position: Int) -> Unit
) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_card, parent, false)
        return CategoryViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as CategoryViewHolder
        val categoryItem = List[position]
        Picasso.get().load(categoryItem.icon).into(holder.imageView)
        holder.firstTxt.text = List[position].name
    }

    override fun getItemCount(): Int {
        return List.size
    }
}

