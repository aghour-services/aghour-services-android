package com.aghourservices.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.PublishedArticleCardBinding
import com.aghourservices.ui.viewHolders.PublishedArticlesViewHolder
import com.aghourservices.utils.ads.NativeAdViewHolder

class PublishedArticlesAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<PublishedArticlesViewHolder>() {
    private var articleList: ArrayList<Article> = ArrayList()
    private var itemsCountToShowAds = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublishedArticlesViewHolder {
        val view =
            PublishedArticleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PublishedArticlesViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: PublishedArticlesViewHolder, position: Int) {
        val article = articleList[position]
        holder.setNewsList(article)

        if (getItemViewType(position) == 0) {
            val adFrame = holder.binding.adFrame
            NativeAdViewHolder(holder.binding.root.context, adFrame)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return (position + 1) % (itemsCountToShowAds + 1)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setArticles(articles: ArrayList<Article>) {
        articleList = articles
        notifyDataSetChanged()
    }

    fun getArticle(position: Int): Article {
        return articleList[position]
    }

    fun deleteArticle(position: Int) {
        articleList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateArticle(position: Int, updatedArticle: Article) {
        articleList[position] = updatedArticle
        notifyItemChanged(position)
    }
}