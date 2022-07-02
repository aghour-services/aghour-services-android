package com.aghourservices.ui.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Search
import com.aghourservices.databinding.SearchResultCardBinding

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

            shareFirm.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${search.name} \n ${search.address} \n ${search.description} \n ${search.phone_number} " +
                                "\n تمت المشاركة من خلال تطبيق أجهور الكبرى للتحميل إضغط هنا -> shorturl.at/xG123 "
                    )
                    type = "text/plain"
                }
                itemView.context.startActivity(Intent.createChooser(sendIntent, "شارك بواسطة.."))
            }
        }
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}