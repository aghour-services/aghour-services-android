package com.aghourservices.news.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R

class ArticlesViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val adFrame: ViewGroup = itemView.findViewById(R.id.ad_frame)
    val description: TextView = itemView.findViewById(R.id.description)
    val date: TextView = itemView.findViewById(R.id.date)

    init {
        itemView.setOnLongClickListener {
            copyClipboard()
        }
    }

    private fun copyClipboard(): Boolean {
        val clipboardManager = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Label", description.text)
        clipboardManager.setPrimaryClip(clip)
        Toast.makeText(itemView.context, "تم نسخ الخبر", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onClick(p0: View?) {
        onItemClicked(absoluteAdapterPosition)
    }
}