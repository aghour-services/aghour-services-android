package com.aghourservices

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.listdesign.view.*
import kotlinx.android.synthetic.main.recycler_view_design.view.*

class listAdapter(context: Context,arrlist:ArrayList<list_data>)
    :ArrayAdapter<list_data>(context,0,arrlist) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val data=LayoutInflater.from(context).inflate(R.layout.listdesign,parent,false)
         val items=getItem(position)
        data.txt1.text=items!!.name
        data.txt2.text=items.desc
        data.circleImageView.setImageResource(items.img)
        return data
    }
}