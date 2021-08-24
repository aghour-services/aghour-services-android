package com.aghourservices.categories.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.categories.api.CategoryItem
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
        Picasso.get().load(icon_url(categoryItem.icon_path)).into(holder.imageView)
        holder.firstTxt.text = List[position].name
    }

    override fun getItemCount(): Int {
        return List.size
    }

    private fun icon_url(icon_name: String): String {
        return "https://aghour-services-backend.herokuapp.com$icon_name"
    }
}

