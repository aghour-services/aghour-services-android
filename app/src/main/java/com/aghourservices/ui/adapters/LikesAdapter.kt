package com.aghourservices.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.User
import com.aghourservices.databinding.LikesCardBinding
import com.aghourservices.ui.viewHolders.LikesViewHolder

class LikesAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<LikesViewHolder>() {
    private var usersList: List<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesViewHolder {
        val view = LikesCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikesViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val user = usersList[position]
        holder.setUsersList(user)

    }

    override fun getItemCount() = usersList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setUsers(users: ArrayList<User>) {
        usersList = users
        notifyDataSetChanged()
    }
}