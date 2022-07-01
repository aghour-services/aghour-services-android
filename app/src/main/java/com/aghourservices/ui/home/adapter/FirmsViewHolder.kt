package com.aghourservices.ui.home.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FirmsCardBinding
import com.aghourservices.utils.helper.Event

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
                        "${firm.name} \n ${firm.address} \n ${firm.description} \n ${firm.phone_number} " + "\nتمت المشاركة من خلال تطبيق أجهور الكبرى "
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