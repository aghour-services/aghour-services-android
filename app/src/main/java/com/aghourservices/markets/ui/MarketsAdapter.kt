package com.aghourservices.markets.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.markets.api.MarketItem
import com.squareup.picasso.Picasso

class MarketsAdapter(
    private val arrayList: ArrayList<MarketItem>,
    private val onItemClicked: (position: Int) -> Unit

) :
    RecyclerView.Adapter<MarketsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.markets_view, parent, false)
        return MarketsViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: MarketsViewHolder, position: Int) {
        val marketItem = arrayList[position]
        Picasso.get().load(marketItem.url).into(holder.imageViewer)
        holder.txtOne.text = arrayList[position].id.toString()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}