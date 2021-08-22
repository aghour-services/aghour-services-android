package com.aghourservices.listView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aghourservices.R
import kotlinx.android.synthetic.main.activity_list_view.*
import kotlinx.android.synthetic.main.list_design.*
import kotlinx.android.synthetic.main.my_toolbar.*

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        setSupportActionBar(myToolbar)

        val arrlist=ArrayList<list_data>()
        arrlist.add(list_data("خالد العبادي","طبيب", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","مهندس", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","صيدلي", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","بائع", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","مبرمج", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","سائق", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","طبيب", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","مهندس", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","صيدلي", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","بائع", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","مبرمج", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","سائق", R.mipmap.img_doctor))
        val myadapter= listAdapter(this,arrlist)
        mylist.adapter=myadapter
    }
}