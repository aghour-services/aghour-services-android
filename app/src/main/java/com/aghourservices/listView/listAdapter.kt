package com.aghourservices.listView

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.aghourservices.R
import kotlinx.android.synthetic.main.list_design.view.*

class listAdapter(context: Context,arrlist:ArrayList<list_data>)
    :ArrayAdapter<list_data>(context,0,arrlist) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val data=LayoutInflater.from(context).inflate(R.layout.list_design,parent,false)
         val items=getItem(position)
        data.txt1.text=items!!.name
        data.txt2.text= items.desc
        data.circleImageView.setImageResource(items.img)
        return data
    }
}