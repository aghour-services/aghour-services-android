package com.aghourservices.firms

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.api.Category
import com.aghourservices.databinding.ActivityAddDataBinding
import com.aghourservices.firms.api.CreateFirm
import com.aghourservices.firms.api.ListFirms
import com.google.android.gms.ads.AdView
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class AddFirm : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    lateinit var adView: AdView
    private lateinit var categoryList: ArrayList<Category>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.addDataToolbar)
        loadCategories()
        spinnerAdapter(categoryList)

        adView = findViewById(R.id.adView)
        AghourAdManager.displayBannerAd(this, adView)


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnAddData.setOnClickListener(View.OnClickListener {
            val selectedCategoryPosition = binding.spinner.selectedItemPosition
            val selectedCategory = categoryList.get(selectedCategoryPosition)

//            Toast.makeText(this, selectedCategory.name.toString(), Toast.LENGTH_LONG).show()
            val name = binding.name.text.toString()
            val address = binding.address.text.toString()
            val description = binding.description.text.toString()
            val phoneNumber = binding.phoneNumber.text.toString()
            val firm = Firm()

            firm.name = name
            firm.address = address
            firm.description = description
            firm.phone_number = phoneNumber
            firm.category_id = selectedCategory.id

            if (firm.inValid()) {
                binding.name.error = "الاسم"
                binding.address.error = "العنوان"
                binding.description.error = "اكتب وصف عن صاحب المكان"
                binding.phoneNumber.error = "اكتب رقم التليفون"

                return@OnClickListener
            } else {
                createFirm(firm)
                Toast.makeText(this, "تم اضافة البيانات", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun spinnerAdapter(categoryList: ArrayList<Category>) {
        var categories = mutableListOf<String>()

        for (item in categoryList) {
            categories.add(item.name!!)
        }
        val spinnerAdapter =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinner)
        {
            adapter = spinnerAdapter
        }
    }


    private fun createFirm(firm: Firm) {
        val user = UserInfo().getUserData(this)
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build().create(CreateFirm::class.java)

        val retrofitData = retrofitBuilder.createFirm(firm.toJsonObject(), user.token)

        retrofitData.enqueue(object : Callback<Firm> {

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Firm>, response: Response<Firm>) {
                Toast.makeText(this@AddFirm, response.code().toString(), Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Firm>, t: Throwable) {

            }
        })
    }

    private fun loadCategories() {
        Realm.init(this)
        val config = RealmConfiguration.Builder().name("category.realm")
            .deleteRealmIfMigrationNeeded().schemaVersion(1)
            .allowWritesOnUiThread(true).build()

        val categoryRealm = Realm.getInstance(config)

        val result = categoryRealm.where(Category::class.java).findAll()
        categoryList = ArrayList()
        categoryList.addAll(result)
    }
}