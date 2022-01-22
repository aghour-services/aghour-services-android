package com.aghourservices.news.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.ads.NativeAdViewHolder
import com.aghourservices.news.Article

class ArticlesAdapter(
    val context: Context,
    private val arrayList: ArrayList<Article>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemsCount = arrayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)
        return ArticlesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hold = holder as ArticlesViewHolder
        val item = arrayList[position]
        hold.description.text = item.description
        hold.date.text = item.created_at
    }

    override fun getItemCount(): Int {
        return itemsCount
    }
}