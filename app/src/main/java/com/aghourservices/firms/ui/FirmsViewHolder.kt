package com.aghourservices.firms.ui

import android.content.ClipDescription
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.provider.Settings.System.getString
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.firms.Firm

class FirmsViewHolder(
    itemView: View, private val onItemClicked: (position: Int) -> Unit,
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var adFrame: ViewGroup = itemView.findViewById(R.id.ad_frame)
    var name: TextView = itemView.findViewById(R.id.name)
    var description: TextView = itemView.findViewById(R.id.description)
    var address: TextView = itemView.findViewById(R.id.address)
    var callButton: Button = itemView.findViewById(R.id.btnCall)
    var shareFirm: TextView = itemView.findViewById(R.id.shareFirm)
    var favorite: CheckBox = itemView.findViewById(R.id.fav)
    private lateinit var mediaPlayer: MediaPlayer

    fun setList(firm: Firm) {
        name.text = firm.name
        address.text = firm.address
        description.text = firm.description
        callButton.text = firm.phone_number

        callButton.setOnClickListener(this)

        shareFirm.setOnClickListener {
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
            mediaPlayer = MediaPlayer.create(itemView.context, R.raw.facebook_share)
            mediaPlayer.start()
        }
        favorite.setOnClickListener{
            mediaPlayer = MediaPlayer.create(itemView.context, R.raw.like)
            mediaPlayer.start()
        }
    }

    override fun onClick(v: View) {
        onItemClicked(absoluteAdapterPosition)
    }
}