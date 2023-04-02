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
import com.aghourservices.databinding.ArticleCardBinding
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.utils.ads.NativeAdViewHolder
import com.aghourservices.utils.helper.Intents
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

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

    fun updateArticle(position: Int, updatedArticle: Article) {
        articleList[position] = updatedArticle
        notifyItemChanged(position)
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
            binding.articleCountLayout.setOnClickListener(this)
            binding.latestCommentCard.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun setNewsList(article: Article) {
            val profile = UserInfo.getUserID(binding.root.context)

            article.attachments?.forEach { attachment ->
                binding.articleImage.isVisible = true
                Glide.with(binding.root.context)
                    .load(attachment.resource_url)
                    .placeholder(R.color.image_bg)
                    .error(R.drawable.ic_error)
                    .encodeQuality(100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.articleImage)
            }

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

            binding.likesCount.text = article.likes_count.toString()
            binding.commentsCount.text = article.comments_count.toString()
            binding.date.text = article.created_at
            binding.likeArticle.isChecked = article.liked

            binding.commentsCard.isVisible =
                !article.latest_comment?.user?.name.isNullOrEmpty() && !article.latest_comment?.body.isNullOrEmpty()

            if (binding.commentsCard.isVisible) {
                val commentUser = article.latest_comment?.user
                binding.name.apply {
                    text = commentUser?.name
                    if (commentUser?.is_verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tooltipText = context.getString(R.string.verified)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                }
                binding.body.text = article.latest_comment?.body
                binding.commentTime.text = article.latest_comment?.created_at
            }

            binding.shareArticle.setOnClickListener {
                Intents.shareNews(article.description, itemView)
            }

            if (article.user?.id == profile.id) {
                binding.popupMenu.isVisible = true
            }

            if (article.likes_count > 0) {
                binding.likesCount.text = if (article.liked) {
                    if (article.likes_count == 1) {
                        "أنت"
                    } else {
                        "أنت و ${article.likes_count - 1} أخرين "
                    }
                } else {
                    article.likes_count.toString()
                }
            }
        }

        override fun onClick(v: View) {
            onItemClicked(v, absoluteAdapterPosition)
        }
    }
}