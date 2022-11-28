package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.NewsCardBinding
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.utils.ads.NativeAdViewHolder
import com.aghourservices.utils.helper.Intents

class ArticlesAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<ArticlesAdapter.ArticlesViewHolder>() {
    private var articleList: ArrayList<Article> = ArrayList()
    private var itemsCountToShowAds = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        val view = NewsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticlesViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val article = articleList[position]
        holder.setNewsList(article)

        if (getItemViewType(position) == 0) {
            NativeAdViewHolder(holder.binding.root.context, holder.binding.adFrame)
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

    inner class ArticlesViewHolder(
        val binding: NewsCardBinding,
        private val onItemClicked: (v: View, position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.commentTv.setOnClickListener(this)
            binding.newsCardView.setOnClickListener(this)
            binding.newsFavorite.setOnClickListener(this)
        }

        fun setNewsList(article: Article) {
            binding.apply {
                val user = getUserData(root.context)
                userName.text = user.name
                description.text = article.description
                date.text = article.created_at
                newsFavorite.isChecked = article.isFavorite

                description.setOnLongClickListener {
                    Intents.copyNews(article.description, itemView)
                    true
                }

//                shareNews.setOnClickListener {
//                    Intents.shareNews(article.description, itemView)
//                }
            }
        }

        override fun onClick(v: View) {
            onItemClicked(v, absoluteAdapterPosition)
        }
    }
}