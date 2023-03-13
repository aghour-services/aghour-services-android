package com.aghourservices.ui.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.User
import com.aghourservices.databinding.LikesUsersCardBinding

class UsersLikesAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<UsersLikesAdapter.LikesUsersViewHolder>() {
    private var usersList: List<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesUsersViewHolder {
        val view = LikesUsersCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LikesUsersViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: LikesUsersViewHolder, position: Int) {
        val user = usersList[position]
        holder.setUsersList(user)

    }

    override fun getItemCount() = usersList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setUsers(users: ArrayList<User>) {
        usersList = users
        notifyDataSetChanged()
    }

    inner class LikesUsersViewHolder(
        val binding: LikesUsersCardBinding,
        private val onItemClicked: (v: View, position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.userLayout.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun setUsersList(user: User) {
            binding.userName.apply {
                text = user.name
                if (user.id == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tooltipText = "الحساب الرئيسي"
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }
        }

        override fun onClick(v: View) {
            onItemClicked(v, absoluteAdapterPosition)
        }
    }
}