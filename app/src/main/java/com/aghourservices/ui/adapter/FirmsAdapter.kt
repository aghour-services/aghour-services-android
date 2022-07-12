package com.aghourservices.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FirmsCardBinding
import com.aghourservices.databinding.TagsCardBinding

class FirmsAdapter(
    val context: Context,
    private var firmsList: ArrayList<Firm>,
    private val isHorizontalView: Int,
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val THE_FIRST_VIEW = 1
        const val THE_SECOND_VIEW = 2
//        const val itemsCountToShowAds = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val firmsCard = FirmsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val tagsCard = TagsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        if (viewType == THE_FIRST_VIEW) {
            return TagsViewHolder(
                tagsCard, firmsList, onItemClicked
            )
        }
        return FirmsViewHolder(
            firmsCard, firmsList, onItemClicked
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHorizontalView == THE_FIRST_VIEW && holder is TagsViewHolder) {
            holder.setTagsList(position)
        } else {
            if (isHorizontalView == THE_SECOND_VIEW && holder is FirmsViewHolder) {
                holder.setList(position)
            }
        }

//        if (getItemViewType(position) == THE_SECOND_VIEW && holder is FirmsViewHolder) {
//            val adFrame = holder.binding.adFrame
//            NativeAdViewHolder(context, adFrame)
//        }
    }


    override fun getItemViewType(position: Int): Int {
        return isHorizontalView
        //return (position + 1) % (itemsCountToShowAds + 1)
    }

    override fun getItemCount(): Int {
        return firmsList.size
    }
}