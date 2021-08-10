package com.aghourservices.categories

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aghourservices.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.my_toolbar.*


class CategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(myToolbar)

        recyclerview.layoutManager = GridLayoutManager(this, 2)
        val data = ArrayList<ItemsViewModel>()
        val name = "Leo Messi"
        for (i in 0..19) {
            data.add(ItemsViewModel(R.drawable.messi, name + (i + 1)))
        }
        val adapter = CustomAdapter(data)
        recyclerview.adapter = adapter
    }
}