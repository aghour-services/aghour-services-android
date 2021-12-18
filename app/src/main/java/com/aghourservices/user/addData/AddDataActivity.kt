package com.aghourservices.user.addData

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import com.aghourservices.R
import com.aghourservices.databinding.ActivityAddDataBinding

class AddDataActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddDataBinding
    var languages = arrayOf(R.string.choose, "مدرسين", "أطباء", "حرفيين", "مطاعم", "مدرسين", "أطباء", "حرفيين", "مضحكين")
    val NEW_SPINNER_ID = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.addDataToolbar)

        binding.backBtn.setOnClickListener {
            finish()
        }

        val myAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, languages)
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinner)
        {
            adapter = myAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    p0!!.getItemAtPosition(0).toString().isEmpty()
                    if (p0.getItemAtPosition(p2).equals(R.string.choose)) {

                    } else {
                        val item: String = p0.getItemAtPosition(p2).toString()
                        Toast.makeText(this@AddDataActivity, item, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            gravity = Gravity.CENTER
        }
    }
}