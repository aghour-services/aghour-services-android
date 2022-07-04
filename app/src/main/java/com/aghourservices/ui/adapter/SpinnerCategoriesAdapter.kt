package com.aghourservices.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.databinding.SpinnerItemsBinding
import com.squareup.picasso.Picasso

class SpinnerCategoriesAdapter(context: Context, category: List<Category>) :
    ArrayAdapter<Category>(context, 0, category) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, parent)
    }

    private fun initView(position: Int, parent: ViewGroup): View {
        val category = getItem(position)
        val binding = SpinnerItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        val categoryImage = binding.categoryImage
        val categoryName = binding.categoryName

        Picasso.get().load(category?.icon).placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error).into(categoryImage)
        categoryName.text = category?.name

        return binding.root
    }
}
