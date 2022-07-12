package com.aghourservices.ui.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FirmsCardBinding
import com.aghourservices.databinding.TagsCardBinding
import com.aghourservices.utils.helper.Intents.copyFirm
import com.aghourservices.utils.helper.Intents.shareFirm

class TagsViewHolder(
    val binding: TagsCardBinding,
    private var firmsList: ArrayList<Firm>,
    private val onItemClicked: (v: View, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    fun setTagsList(position: Int) {
        val firm = firmsList[position]
        binding.tagTv.setOnClickListener(this)

        binding.apply {
            tagTv.text = firm.name
        }
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}