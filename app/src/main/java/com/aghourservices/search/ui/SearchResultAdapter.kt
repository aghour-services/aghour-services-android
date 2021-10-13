package com.aghourservices.search.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.ads.NativeAdViewHolder
import com.aghourservices.search.api.SearchResult

class SearchResultAdapter(
    val context: Context,
    private val arrayList: ArrayList<SearchResult>,
    private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsCountToShowAds = 4
    private var itemsCount = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result_card, parent, false)
        return SearchResultViewHolder(view, onItemClicked)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val holder = holder as SearchResultViewHolder
        val marketItem = arrayList[position]
        holder.name.text = marketItem.name
        holder.address.text = marketItem.address
        holder.description.text = marketItem.description
        holder.imageButton.text = marketItem.phone_number
        holder.categoryName.text=marketItem.category_name

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