package com.aghourservices.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Search
import com.aghourservices.databinding.SearchResultCardBinding
import com.aghourservices.ui.viewHolders.SearchResultViewHolder
import com.aghourservices.utils.ads.NativeAdViewHolder

class SearchResultAdapter(
    val context: Context,
    private var searchList: ArrayList<Search>,
    private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsCountToShowAds = 1
    private var itemsCount = searchList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val searchFirm =
            SearchResultCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(searchFirm, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hold = holder as SearchResultViewHolder
        val searchResult = searchList[position]
        hold.setList(searchResult)

        if (getItemViewType(position) == 0) {
            NativeAdViewHolder(context, hold.binding.adFrame)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return (position + 1) % (itemsCountToShowAds + 1)
    }

    override fun getItemCount(): Int {
        return itemsCount
    }
}