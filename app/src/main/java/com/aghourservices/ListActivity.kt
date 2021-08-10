package com.aghourservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_list_view.*

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        val arrlist=ArrayList<list_data>()
        arrlist.add(list_data("khaled elabady","vet",R.mipmap.img_doctor))
        arrlist.add(list_data("khaled elabady","vet",R.mipmap.img_doctor))
        arrlist.add(list_data("khaled","vet",R.mipmap.img_doctor))
        arrlist.add(list_data("khaled","vet",R.mipmap.img_doctor))
        arrlist.add(list_data("khaled","vet",R.mipmap.img_doctor))
        val myadapter=listAdapter(this,arrlist)
        mylist.adapter=myadapter

    }
}