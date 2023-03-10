package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.ArticleCardBinding
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.utils.ads.NativeAdViewHolder
import com.aghourservices.utils.helper.Intents

class ArticlesAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<ArticlesAdapter.ArticlesViewHolder>() {
    private var articleList: ArrayList<Article> = ArrayList()
    private var itemsCountToShowAds = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        val view = ArticleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun deleteArticle(position: Int) {
        articleList.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ArticlesViewHolder(
        val binding: ArticleCardBinding,
        private val onItemClicked: (v: View, position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.addComment.setOnClickListener(this)
            binding.popupMenu.setOnClickListener(this)
            binding.userLayout.setOnClickListener(this)
            binding.likeArticle.setOnClickListener(this)
            binding.likesCount.setOnClickListener(this)
            binding.latestCommentCard.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun setNewsList(article: Article) {
            val profile = UserInfo.getUserID(binding.root.context)

            binding.apply {
                userName.text = article.user?.name
                description.text = article.description
                date.text = article.created_at
                likeArticle.isChecked = article.liked
                commentTime.text = article.latest_comment?.created_at

                description.setOnLongClickListener {
                    Intents.copyNews(article.description, itemView)
                    true
                }

                shareArticle.setOnClickListener {
                    Intents.shareNews(article.description, itemView)
                }

                if (article.likes_count < 1) {
                    likesCount.text = "لا توجد إعجابات"
                } else {
                    likesCount.text = "${article.likes_count} إعجاب"
                }

                if (article.latest_comment?.user?.name == null || article.latest_comment?.body == null) {
                    commentsCard.isVisible = false
                } else {
                    commentsCard.isVisible = true
                    name.text = article.latest_comment?.user?.name
                    body.text = article.latest_comment?.body
                }

                if (article.user?.id != profile.id) {
                    binding.apply {
                        popupMenu.visibility = View.GONE
                    }
                }
            }
        }

        override fun onClick(v: View) {
            onItemClicked(v, absoluteAdapterPosition)
        }
    }
}