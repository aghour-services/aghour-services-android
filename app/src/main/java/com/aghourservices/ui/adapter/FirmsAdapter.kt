package com.aghourservices.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FirmsCardBinding
import com.aghourservices.utils.ads.NativeAdViewHolder

class FirmsAdapter(
    val context: Context,
    private var firmsList: ArrayList<Firm>,
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemsCountToShowAds = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val firmsCard = FirmsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return FirmsViewHolder(
            firmsCard, firmsList, onItemClicked
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as FirmsViewHolder
        holder.setList(position)


        if (getItemViewType(position) == 0) {
            val adFrame = holder.binding.adFrame
            NativeAdViewHolder(context, adFrame)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return (position + 1) % (itemsCountToShowAds + 1)
    }

    override fun getItemCount(): Int {
        return firmsList.size
    }
}