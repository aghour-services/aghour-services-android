package com.aghourservices.ui.viewHolders

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.PublishedArticleCardBinding
import com.aghourservices.utils.helper.Intents
import com.aghourservices.utils.services.cache.UserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PublishedArticlesViewHolder(
    val binding: PublishedArticleCardBinding,
    private val onItemClicked: (v: View, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        binding.addComment.setOnClickListener(this)
        binding.popupMenu.setOnClickListener(this)
        binding.userLayout.setOnClickListener(this)
        binding.likeArticle.setOnClickListener(this)
        binding.latestCommentCard.setOnClickListener(this)
        binding.likesCount.setOnClickListener(this)
        binding.commentsCount.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun setNewsList(article: Article) {
        val profile = UserInfo.getProfile(binding.root.context)
        val avatarUrl = article.user?.url

        Log.d("AVATAR", "setNewsList: $avatarUrl")

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
            if (article.user?.verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                if (commentUser?.verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tooltipText = context.getString(R.string.verified)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }
            binding.body.text = article.latest_comment?.body
            binding.commentTime.text = article.latest_comment?.created_at

            Glide.with(binding.root.context)
                .load(avatarUrl)
                .placeholder(R.mipmap.user)
                .encodeQuality(100)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.commentAvatar)
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

        Glide.with(binding.root.context)
            .load(avatarUrl)
            .placeholder(R.mipmap.user)
            .encodeQuality(100)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.avatarImage)
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}