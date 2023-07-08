package com.aghourservices.ui.viewHolders

import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentCardBinding
import com.aghourservices.utils.helper.Intents.loadProfileImage
import com.aghourservices.utils.services.cache.UserInfo

class CommentsViewHolder(
    val binding: CommentCardBinding,
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        binding.popupMenu.setOnClickListener(this)
    }

    fun setCommentView(comment: Comment) {
        val avatarUrl = comment.user?.url
        val profile = UserInfo.getProfile(binding.root.context)

        binding.apply {
            body.text = comment.body
            userName.text = comment.user?.name
            time.text = comment.created_at
        }

        binding.userName.apply {
            if (comment.user?.verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tooltipText = context.getString(R.string.verified)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }

        if (comment.user?.id == profile.id) {
            binding.popupMenu.isVisible = true
        }

        loadProfileImage(
            binding.root.context,
            avatarUrl.toString(),
            binding.avatarImage,
        )
    }

    override fun onClick(v: View?) {
        onItemClicked(v!!, absoluteAdapterPosition)
    }
}