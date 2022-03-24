package com.aghourservices.favorite

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.favorite.model.FavoriteEntity

class FavoriteViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var name: TextView = itemView.findViewById(R.id.name)
    var description: TextView = itemView.findViewById(R.id.description)
    var address: TextView = itemView.findViewById(R.id.address)
    var callButton: Button = itemView.findViewById(R.id.btnCall)

    fun setFavoriteList(fe: FavoriteEntity) {
        name.text = fe.name
        address.text = fe.address
        description.text = fe.description
        callButton.text = fe.phone_number

        callButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        onItemClicked(absoluteAdapterPosition)
    }
}