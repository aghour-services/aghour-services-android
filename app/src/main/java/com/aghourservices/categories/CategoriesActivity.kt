package com.aghourservices.categories

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aghourservices.ListActivity
import com.aghourservices.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.my_toolbar.*


class CategoriesActivity : AppCompatActivity() {
    val data = ArrayList<ItemsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(myToolbar)

        recyclerview.layoutManager = GridLayoutManager(this, 2)
        val messi = "Leo Messi"
        for (i in 0..9) {
            data.add(ItemsViewModel(R.drawable.messi, messi + (i + 1)))
        }
        val adapter = CustomAdapter(data) { position -> onListItemClick(position) }
        recyclerview.adapter = adapter
    }

    private fun onListItemClick(position: Int) {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }
}