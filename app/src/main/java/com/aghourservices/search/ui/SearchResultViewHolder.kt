package com.aghourservices.search.ui

import android.content.Intent
import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.firms.Firm
import com.aghourservices.search.api.SearchResult

class SearchResultViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var adFrame: ViewGroup = itemView.findViewById(R.id.ad_frame)
    var name: TextView = itemView.findViewById(R.id.name)
    var description: TextView = itemView.findViewById(R.id.description)
    var address: TextView = itemView.findViewById(R.id.address)
    var categoryName: TextView = itemView.findViewById(R.id.category_name)
    var callButton: Button = itemView.findViewById(R.id.btnCall)
    var shareFirm: TextView = itemView.findViewById(R.id.shareFirm)
    var favorite: CheckBox = itemView.findViewById(R.id.fav)

    fun setList(searchResult: SearchResult) {
        name.text = searchResult.name
        address.text = searchResult.address
        description.text = searchResult.description
        callButton.text = searchResult.phone_number
        categoryName.text = searchResult.category_name

        callButton.setOnClickListener(this)

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

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}