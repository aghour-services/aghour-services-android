package com.aghourservices.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FirmsCardBinding
import com.aghourservices.utils.helper.Intents.copyFirm
import com.aghourservices.utils.helper.Intents.shareFirm

class FirmsViewHolder(
    val binding: FirmsCardBinding,
    private var firmsList: ArrayList<Firm>,
    private val onItemClicked: (v: View, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    fun setList(position: Int) {
        val firm = firmsList[position]
        binding.btnCall.setOnClickListener(this)
        binding.btnFav.setOnClickListener(this)

        binding.apply {
            name.text = firm.name
            address.text = firm.address
            description.text = firm.description
            btnCall.text = firm.phone_number
            btnFav.isChecked = firm.isFavorite

            copyFirm.setOnClickListener {
                copyFirm(firm.name, firm.address, firm.description, firm.phone_number, itemView)
            }

            shareFirm.setOnClickListener {
                shareFirm(firm.name, firm.address, firm.description, firm.phone_number, itemView)
            }
        }
    }

    override fun onClick(v: View) {
        onItemClicked(v, absoluteAdapterPosition)
    }
}