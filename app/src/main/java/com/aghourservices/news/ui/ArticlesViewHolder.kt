package com.aghourservices.news.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.databinding.NewsCardBinding
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.news.api.Article

class ArticlesViewHolder(
    val binding: NewsCardBinding, private val onItemClicked: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {


    init {
        itemView.setOnClickListener(this)
    }

    fun setNewsList(article: Article) {

        binding.apply {
            description.text = article.description

            date.text = article.created_at
            showMore.isChecked = false

            showMore.setOnClickListener {
                showMore.isChecked = !showMore.isChecked
                if (showMore.isChecked) {
                    description.maxLines = 1000
                    showMore.text = "رؤية أقل"
                } else {
                    description.maxLines = 7
                    showMore.text = "رؤية المزيد"
                }
            }

            shareNews.setOnClickListener {
                Event.sendFirebaseEvent("Share_news", "")
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${article.description}\nتم مشاركة هذا الخبر من تطبيق خدمات أجهور الكبرى\nلتحميل التطبيق اضغط هنا-> shorturl.at/xG123"
                    )
                    type = "text/plain"
                }
                itemView.context.startActivity(Intent.createChooser(shareIntent, "شارك الخبر"))
            }

            newsCardView.setOnLongClickListener {
                val alertDialogBuilder = AlertDialog.Builder(itemView.context, R.style.my_dialog)
                alertDialogBuilder.setCancelable(true)
                alertDialogBuilder.setPositiveButton(R.string.news_share) { _, _ ->
                    val clipboardManager =
                        itemView.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("News", article.description)
                    clipboardManager.setPrimaryClip(clip)
                    Toast.makeText(itemView.context, "تم نسخ الخبر", Toast.LENGTH_SHORT).show()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 16F
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextAppearance(R.style.SegoeTextRegular)
                }
                return@setOnLongClickListener true
            }
        }
    }

    override fun onClick(p0: View?) {
        onItemClicked(absoluteAdapterPosition)
    }
}


