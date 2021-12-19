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
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.databinding.ActivityAddDataBinding
import com.aghourservices.user.api.User
import com.google.android.gms.ads.AdView

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    lateinit var adView: AdView
    var languages = arrayOf(
        "أختر التصنيف",
        "مدرسين",
        "أطباء",
        "حرفيين",
        "مطاعم",
        "مدرسين",
        "أطباء",
        "حرفيين",
        "محامين"
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.addDataToolbar)
        spinnerAdapter()

        adView = findViewById(R.id.adView)
        AghourAdManager.displayBannerAd(this, adView)


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnAddData.setOnClickListener(View.OnClickListener {
            if (binding.name.text.toString().trim().isEmpty() || binding.address.text.toString()
                    .trim()
                    .isEmpty() || binding.description.text.toString().trim()
                    .isEmpty() || binding.phoneNumber.text.toString().trim().isEmpty()
            ) {
                binding.name.error = "الاسم"
                binding.address.error = "العنوان"
                binding.description.error = "اكتب وصف عن صاحب المكان"
                binding.phoneNumber.error = "اكتب رقم التليفون"

                return@OnClickListener
            } else {
                Toast.makeText(this, "تم اضافة البيانات", Toast.LENGTH_LONG).show()
                binding.name.setText("")
                binding.address.setText("")
                binding.description.setText("")
                binding.phoneNumber.setText("")
            }
        })
    }

    private fun spinnerAdapter() {
        val myAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, languages)
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinner)
        {
            adapter = myAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    p0!!.getItemAtPosition(0).toString().isEmpty()
                    if (p0.getItemAtPosition(p2).equals("أختر التصنيف")) {
                    } else {
                        val item: String = p0.getItemAtPosition(p2).toString()
                        Toast.makeText(this@AddDataActivity, item, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
            gravity = Gravity.CENTER
        }
    }
}