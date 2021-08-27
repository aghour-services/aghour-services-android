package com.aghourservices.firms.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.firms.api.FirmItem

class FirmsAdapter(
    private val arrayList: ArrayList<FirmItem>,
    private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<FirmsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.firms_card, parent, false)
        return FirmsViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: FirmsViewHolder, position: Int) {
        val marketItem = arrayList[position]
        holder.name.text = marketItem.name
        holder.description.text = marketItem.description
        holder.address.text = marketItem.address
        holder.imageButton.text = marketItem.phone_number

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}