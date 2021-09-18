package com.aghourservices.firms.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.ads.NativeAdViewHolder
import com.aghourservices.categories.ui.CategoryViewHolder
import com.aghourservices.firms.api.FirmItem

class FirmsAdapter(
    val context: Context,
    private val arrayList: ArrayList<FirmItem>,
    private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsCountToShowAds = 4
    private var itemsCount = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.firms_card, parent, false)
        return FirmsViewHolder(view, onItemClicked)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val holder = holder as FirmsViewHolder
        val marketItem = arrayList[position]
        holder.name.text = marketItem.name
        holder.description.text = marketItem.description
        holder.address.text = marketItem.address
        holder.imageButton.text = marketItem.phone_number

        if (getItemViewType(position) == 0) {
            NativeAdViewHolder(context, holder.adFrame)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return (position + 1) % (itemsCountToShowAds + 1)
    }

    override fun getItemCount(): Int {
        return itemsCount
    }
}