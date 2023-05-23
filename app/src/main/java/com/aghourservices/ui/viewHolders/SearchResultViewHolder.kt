package com.aghourservices.ui.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Search
import com.aghourservices.databinding.SearchResultCardBinding
import com.aghourservices.utils.helper.Intents

class SearchResultViewHolder(
    val binding: SearchResultCardBinding, private val onItemClicked: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    fun setList(search: Search) {
        binding.btnCall.setOnClickListener(this)

        binding.apply {
            name.text = search.name
            address.text = search.address
            description.text = search.description
            btnCall.text = search.phone_number
            categoryName.text = search.category_name

            copyFirm.setOnClickListener {
                Intents.copyFirm(
                    search.name,
                    search.address,
                    search.description,
                    search.phone_number,
                    itemView
                )
            }

            shareFirm.setOnClickListener {
                Intents.shareFirm(
                    search.name,
                    search.address,
                    search.description,
                    search.phone_number,
                    itemView
                )
            }
        }
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}