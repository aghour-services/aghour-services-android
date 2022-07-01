package com.aghourservices.utils.ads

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NativeAdViewHolder(
    val context: Context,
    itemView: ViewGroup
) : RecyclerView.ViewHolder(itemView) {
    init {
        Native.load(context, itemView)
    }
}