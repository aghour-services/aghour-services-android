package com.aghourservices.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.databinding.NewsCardBinding
import com.aghourservices.utils.helper.Intents.copyNews
import com.aghourservices.utils.helper.Intents.shareNews

class ArticlesViewHolder(
    val binding: NewsCardBinding,
    private val onItemClicked: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    fun setNewsList(article: com.aghourservices.data.model.Article) {

        binding.apply {
            description.text = article.description
            date.text = article.created_at

            copyNews.setOnClickListener {
                copyNews(article.description, itemView)
            }

            shareNews.setOnClickListener {
                shareNews(article.description, itemView)
            }
        }
    }

    override fun onClick(p0: View?) {
        onItemClicked(absoluteAdapterPosition)
    }
}


