package com.aghourservices.ui.viewHolders

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.data.model.User
import com.aghourservices.databinding.LikesCardBinding

class LikesViewHolder (
    val binding: LikesCardBinding,
    private val onItemClicked: (v: View, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        binding.userLayout.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun setUsersList(user: User) {
        binding.userName.apply {
            text = user.name
            if (user.is_verified && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tooltipText = context.getString(R.string.verified)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}