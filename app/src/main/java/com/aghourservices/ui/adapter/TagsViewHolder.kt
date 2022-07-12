package com.aghourservices.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Tag
import com.aghourservices.databinding.TagsCardBinding

class TagsViewHolder(
    val binding: TagsCardBinding,
    private var tagsList: ArrayList<Tag>,
    private val onItemClicked: (v: View, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    fun setTagsList(position: Int) {
        val tag = tagsList[position]
        binding.tagTv.setOnClickListener(this)

        binding.apply {
            tagTv.text = tag.tag
        }
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}