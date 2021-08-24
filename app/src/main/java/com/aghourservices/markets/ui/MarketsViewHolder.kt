package com.aghourservices.markets.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import kotlinx.android.synthetic.main.markets_view.view.*

class MarketsViewHolder (itemView: View) :
    RecyclerView.ViewHolder(itemView) {

        val imageViewer: ImageView = itemView.findViewById(R.id.image)
        var TxtOne: TextView = itemView.TxtOne
    }