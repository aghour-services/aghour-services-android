package com.aghourservices.firms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.aghourservices.R
import com.aghourservices.categories.api.Category

class SpinnerAdapter(context: Context, category: List<Category>) :
    ArrayAdapter<Category>(context, 0, category) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, parent)
    }

    private fun initView(position: Int, parent: ViewGroup): View {
        val category = getItem(position)
        val view = LayoutInflater.from(context).inflate(R.layout.spinner_items, parent, false)
        val categoryImage = view.findViewById<ImageView>(R.id.categoryImage)
        val categoryName = view.findViewById<TextView>(R.id.categoryName)

        categoryImage.setImageResource(category?.icon!!.toInt())
        categoryName.text = category.name

        return view
    }
}
