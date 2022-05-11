package com.aghourservices.search.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.databinding.SearchResultCardBinding
import com.aghourservices.search.api.SearchResult

class SearchResultViewHolder(
    val binding: SearchResultCardBinding, private val onItemClicked: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {


    fun setList(searchResult: SearchResult) {
        binding.btnCall.setOnClickListener(this)

        binding.apply {
            name.text = searchResult.name
            address.text = searchResult.address
            description.text = searchResult.description
            btnCall.text = searchResult.phone_number
            categoryName.text = searchResult.category_name

            shareFirm.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${searchResult.name} \n ${searchResult.address} \n ${searchResult.description} \n ${searchResult.phone_number} " +
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