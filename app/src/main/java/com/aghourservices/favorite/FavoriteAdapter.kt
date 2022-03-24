package com.aghourservices.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.favorite.model.FavoriteEntity
import com.aghourservices.firms.Firm
import com.aghourservices.firms.ui.FirmsViewHolder

class FavoriteAdapter(
    private val favoriteList: ArrayList<FavoriteEntity>,
    var context: Context,
    private val onItemClicked: (position: Int) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_card, parent, false)
        return FavoriteViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hold = holder as FavoriteViewHolder
        val favoriteList = favoriteList[position]
        hold.setFavoriteList(favoriteList)
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }
}