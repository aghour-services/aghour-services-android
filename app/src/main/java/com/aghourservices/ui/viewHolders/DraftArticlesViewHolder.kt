package com.aghourservices.ui.viewHolders

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.DraftArticleCardBinding
import com.aghourservices.utils.helper.Intents
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DraftArticlesViewHolder(
    val binding: DraftArticleCardBinding,
    private val onItemClicked: (v: View, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        binding.userLayout.setOnClickListener(this)
        binding.publishDraftArticleBtn.setOnClickListener(this)
        binding.draftArticlePopupMenu.setOnClickListener(this)
        binding.userName.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun setArticlesList(article: Article) {
        val avatarUrl = article.user?.url

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

        binding.date.text = article.created_at

        Intents.loadProfileImage(
            binding.root.context,
            avatarUrl.toString(),
            binding.avatarImage,
        )
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}