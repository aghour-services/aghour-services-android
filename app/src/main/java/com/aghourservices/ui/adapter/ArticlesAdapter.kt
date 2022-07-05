package com.aghourservices.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.NewsCardBinding
import com.aghourservices.utils.ads.NativeAdViewHolder

class ArticlesAdapter(
    val context: Context,
    private var newsList: ArrayList<Article>,
    private val onItemClicked: (position: Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsCountToShowAds = 2
    private var itemsCount = newsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = NewsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticlesViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hold = holder as ArticlesViewHolder
        val article = newsList[position]
        hold.setNewsList(article)

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