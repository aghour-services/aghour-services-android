package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.DraftArticleCardBinding
import com.aghourservices.utils.helper.Intents
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DraftArticlesAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<DraftArticlesAdapter.ArticlesViewHolder>() {
    private var articleList: ArrayList<Article> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        val view =
            DraftArticleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticlesViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val article = articleList[position]
        holder.setNewsList(article)
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

    inner class ArticlesViewHolder(
        val binding: DraftArticleCardBinding,
        private val onItemClicked: (v: View, position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.userLayout.setOnClickListener(this)
            binding.publishDraftArticleBtn.setOnClickListener(this)
            binding.draftArticlePopupMenu.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun setNewsList(article: Article) {
            article.attachments?.forEach { attachment ->
                Glide.with(binding.root.context)
                    .load(attachment.resource_url)
                    .placeholder(R.color.image_bg)
                    .encodeQuality(100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.articleImage)
            }
            binding.articleImage.isVisible = article.attachments!!.isNotEmpty()

            binding.userName.apply {
                text = article.user?.name
                if (article.user?.is_verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tooltipText = context.getString(R.string.verified)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }

            binding.description.apply {
                text = article.description
                setOnLongClickListener {
                    Intents.copyNews(article.description, itemView)
                    true
                }
            }

            binding.date.text = article.created_at

        }

        override fun onClick(v: View) {
            onItemClicked(v, absoluteAdapterPosition)
        }
    }
}