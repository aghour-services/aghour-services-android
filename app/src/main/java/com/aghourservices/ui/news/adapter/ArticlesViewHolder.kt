package com.aghourservices.ui.news.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.databinding.NewsCardBinding
import com.aghourservices.utils.helper.Event

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

            shareNews.setOnClickListener {
                Event.sendFirebaseEvent("Share_news", "")
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        article.description + "\n تمت المشاركة من خلال تطبيق أجهور الكبرى "
                    )
                    type = "text/plain"
                }
                itemView.context.startActivity(Intent.createChooser(shareIntent, "شارك الخبر.."))
            }
        }
    }

    override fun onClick(p0: View?) {
        onItemClicked(absoluteAdapterPosition)
    }
}


