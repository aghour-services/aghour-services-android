package com.aghourservices.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Notification
import com.aghourservices.databinding.NotificationCardBinding
import com.aghourservices.ui.viewHolders.NotificationsViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class NotificationsAdapter(
    private val onItemClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<NotificationsViewHolder>() {
    private var notificationList: ArrayList<Notification> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val notificationCard =
            NotificationCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationsViewHolder(notificationCard, onItemClicked)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val notificationItems = notificationList[position]
        val userAvatar = notificationItems.user?.url
        val articleImage = notificationItems.articleImageUrl

        Log.d("USER AVATAR", "onBindViewHolder: $articleImage")

        holder.binding.apply {
            titleTv.text = notificationItems.title
            bodyTv.text = notificationItems.body
            timeAgoTv.text = notificationItems.timeAgo
            Glide.with(root.context)
                .load(userAvatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userAvatarTv)

            Glide.with(root.context)
                .load(articleImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(articleImageTv)
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNotifications(notifications: ArrayList<Notification>) {
        notificationList = notifications
        notifyDataSetChanged()
    }

    fun getNotification(position: Int): Notification {
        return notificationList[position]
    }
}

