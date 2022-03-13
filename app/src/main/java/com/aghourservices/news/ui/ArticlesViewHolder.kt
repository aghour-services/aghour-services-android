package com.aghourservices.news.ui

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.news.api.Article


class ArticlesViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit,
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val adFrame: ViewGroup = itemView.findViewById(R.id.ad_frame)
    val description: TextView = itemView.findViewById(R.id.description)
    private val date: TextView = itemView.findViewById(R.id.date)
    private val shareButton: TextView = itemView.findViewById(R.id.shareNews)
    private val newsCard: LinearLayout = itemView.findViewById(R.id.news_card_view)

    fun setNewsList(article: Article) {
        description.text = article.description
        date.text = article.created_at

        shareButton.setOnClickListener {
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
        newsCard.setOnLongClickListener {
            val alertDialogBuilder = AlertDialog.Builder(itemView.context, R.style.my_dialog)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton(R.string.news_share) { _, _ ->
                val clipboardManager =
                    itemView.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("News", article.description)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(itemView.context,"تم نسخ الخبر",Toast.LENGTH_SHORT).show()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 20F
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextAppearance(R.style.SegoeTextBold)
            }
            return@setOnLongClickListener true
        }
    }

    override fun onClick(p0: View?) {
        onItemClicked(absoluteAdapterPosition)
    }
}


