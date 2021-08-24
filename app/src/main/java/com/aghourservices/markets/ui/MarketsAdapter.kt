package com.aghourservices.markets.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.markets.marketsApi.MarketItem
import com.squareup.picasso.Picasso

class MarketsAdapter(
    private val arrayList: ArrayList<MarketItem>
) :
    RecyclerView.Adapter<MarketsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.markets_view, parent, false)
        return MarketsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarketsViewHolder, position: Int) {
        val MarketItem = arrayList[position]
        Picasso.get().load(MarketItem.url).into(holder.imageViewer)
        holder.TxtOne.text = arrayList[position].id.toString()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}