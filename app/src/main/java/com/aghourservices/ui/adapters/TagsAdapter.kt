package com.aghourservices.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Tag
import com.aghourservices.databinding.TagsCardBinding
import com.aghourservices.ui.viewHolders.TagsViewHolder

class TagsAdapter(
    val context: Context,
    private var tagsList: ArrayList<Tag>,
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val tagsCard = TagsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TagsViewHolder(tagsCard, tagsList, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as TagsViewHolder
        holder.setTagsList(position)
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }
}