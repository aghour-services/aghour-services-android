package com.aghourservices.firms.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R

class FirmsViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var adFrame: ViewGroup = itemView.findViewById(R.id.ad_frame)
    var name: TextView = itemView.findViewById(R.id.name)
    var description: TextView = itemView.findViewById(R.id.description)
    var address: TextView = itemView.findViewById(R.id.address)
    var callButton: Button = itemView.findViewById(R.id.btnCall)

    init {
        callButton.setOnClickListener(this)
        callButton.setOnLongClickListener {
            copyClipboard()
        }
    }

    private fun copyClipboard(): Boolean {
        val clipboardManager = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Label", callButton.text)
        clipboardManager.setPrimaryClip(clip)
        Toast.makeText(itemView.context, "تم نسخ رقم الهاتف", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}