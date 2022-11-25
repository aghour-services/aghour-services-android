package com.aghourservices.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.NewsCardBinding
import com.aghourservices.utils.helper.Intents.copyNews
import com.aghourservices.utils.helper.Intents.shareNews

class ArticlesViewHolder(
    val binding: NewsCardBinding,
    private val onItemClicked: (v: View, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        binding.commentNews.setOnClickListener(this)
        binding.newsCardView.setOnClickListener(this)
    }

    fun setNewsList(article: Article) {
        binding.apply {
            description.text = article.description
            date.text = article.created_at

            newsCardView.setOnLongClickListener {
                copyNews(article.description, itemView)
                true
            }

            shareNews.setOnClickListener {
                shareNews(article.description, itemView)
            }
        }
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}


