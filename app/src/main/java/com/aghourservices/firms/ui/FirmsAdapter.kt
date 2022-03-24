package com.aghourservices.firms.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.ads.NativeAdViewHolder
import com.aghourservices.firms.Firm

class FirmsAdapter(
    val context: Context,
    private val arrayList: ArrayList<Firm>,
    private val onItemClicked: (position: Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsCountToShowAds = 2
    private var itemsCount = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firms_card, parent, false)
        return FirmsViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hold = holder as FirmsViewHolder
        val firm = arrayList[position]
        hold.setList(firm)

        if (getItemViewType(position) == 0) {
            NativeAdViewHolder(context, hold.adFrame)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return (position + 1) % (itemsCountToShowAds + 1)
    }

    override fun getItemCount(): Int {
        return itemsCount
    }
}