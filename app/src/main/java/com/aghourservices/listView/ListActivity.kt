package com.aghourservices.listView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aghourservices.R
import kotlinx.android.synthetic.main.activity_list_view.*

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        val arrlist=ArrayList<list_data>()
        arrlist.add(list_data("خالد العبادي","طبيب", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","مهندس", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","صيدلي", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","بائع", R.mipmap.img_doctor))
        arrlist.add(list_data("خالد العبادي","مبرمج", R.mipmap.img_doctor))
        val myadapter= listAdapter(this,arrlist)
        mylist.adapter=myadapter

    }
}