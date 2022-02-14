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
    private var itemsCountToShowAds = 2
    private var itemsCount = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_card, parent, false)
        return SearchResultViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hold = holder as SearchResultViewHolder
        val item = arrayList[position]
        hold.name.text = item.name
        hold.address.text = item.address
        hold.description.text = item.description
        hold.imageButton.text = item.phone_number
        hold.categoryName.text = item.category_name

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