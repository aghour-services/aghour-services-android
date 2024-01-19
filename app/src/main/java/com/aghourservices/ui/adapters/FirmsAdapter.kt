package com.aghourservices.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FirmsCardBinding
import com.aghourservices.ui.viewHolders.FirmsViewHolder
import com.aghourservices.utils.ads.NativeAdViewHolder

class FirmsAdapter(
    val context: Context,
    private var firmsList: ArrayList<Firm>,
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<FirmsViewHolder>() {

    private val itemsCountToShowAds = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmsViewHolder {
        val firmsCard = FirmsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return FirmsViewHolder(
            firmsCard, firmsList, onItemClicked
        )
    }

    override fun onBindViewHolder(holder: FirmsViewHolder, position: Int) {
        holder.setList(position)

        if (getItemViewType(position) == 0) {
            val adFrame = holder.binding.adFrame
            NativeAdViewHolder(holder.binding.root.context, adFrame)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return (position + 1) % (itemsCountToShowAds + 1)
    }

    override fun getItemCount(): Int {
        return firmsList.size
    }
}