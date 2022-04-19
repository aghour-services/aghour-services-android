package com.aghourservices.firms.ui

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.databinding.FirmsCardBinding
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.firms.Firm

class FirmsViewHolder(
    val binding: FirmsCardBinding, private val onItemClicked: (v: View, position: Int) -> Unit,
) :RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    fun setList(firm: Firm) {
        binding.btnCall.setOnClickListener(this)
        binding.btnFav.setOnClickListener(this)

        binding.apply {
            name.text = firm.name
            address.text = firm.address
            description.text = firm.description
            btnCall.text = firm.phone_number
            btnFav.isChecked = firm.isFavorite

            shareFirm.setOnClickListener {
                val eventName = "share_${firm.name}"
                Event.sendFirebaseEvent(eventName, "")
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${firm.name} \n ${firm.address} \n ${firm.description} \n ${firm.phone_number} " +
                                "\n تمت المشاركة من خلال تطبيق أجهور الكبرى للتحميل إضغط هنا -> shorturl.at/xG123 "
                    )
                    type = "text/plain"
                }
                itemView.context.startActivity(Intent.createChooser(sendIntent, "شارك بواسطة.."))
            }
        }
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}