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
import com.aghourservices.utils.helper.Intents.loadProfileImage
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
        binding.userName.setOnClickListener(this)
        binding.avatarImage.setOnClickListener(this)
        binding.articleImage.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun setNewsList(article: Article) {
        val currentProfile = UserInfo.getProfile(binding.root.context)
        val avatarUrl = article.user?.url
        val articleUserId = article.user?.id

        Log.d("USER", "setNewsList: ${currentProfile.id}")

        article.attachments?.forEach { attachment ->
            Glide.with(binding.root.context)
                .load(attachment.resource_url)
                .placeholder(R.color.image_bg)
                .encodeQuality(100)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.articleImage)
        }

        binding.articleImage.isVisible = article.attachments!!.isNotEmpty()
        binding.popupMenu.isVisible = currentProfile.id == articleUserId || currentProfile.verified
        binding.commentsCard.isVisible =
            !article.latest_comment?.user?.name.isNullOrEmpty() && !article.latest_comment?.body.isNullOrEmpty()

        // Likes & Comments
        binding.apply {
            likesCount.isVisible = article.likes_count > 0
            if (article.comments_count < 1) {
                commentsCount.text = "لا توجد تعليقات"
            } else {
                commentsCount.text = article.comments_count.toString()
            }
            likesCount.text = "${article.likes_count} إعجاب"
            likeArticle.isChecked = article.liked
        }

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

        binding.date.text = article.created_at


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

            loadProfileImage(
                binding.root.context,
                commentUser?.url,
                binding.commentAvatar,
            )
        }

        binding.shareArticle.setOnClickListener {
            Intents.shareNews(article.description, itemView)
        }


        loadProfileImage(
            binding.root.context,
            avatarUrl.toString(),
            binding.avatarImage,
        )
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}