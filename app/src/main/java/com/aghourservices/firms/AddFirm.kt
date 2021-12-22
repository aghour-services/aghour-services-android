package com.aghourservices.firms

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.api.Category
import com.aghourservices.databinding.ActivityAddDataBinding
import com.aghourservices.firms.api.CreateFirm
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
        Banner.show(this, adView)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnAddData.setOnClickListener(View.OnClickListener {
            val selectedCategoryPosition = binding.spinner.selectedItemPosition
            val selectedCategory = categoryList[selectedCategoryPosition]

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
            }
        })
    }

    private fun spinnerAdapter(categoryList: ArrayList<Category>) {
        val categories = mutableListOf<String>()

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
                dataAdded()
            }

            override fun onFailure(call: Call<Firm>, t: Throwable) {
                noInternet()
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

    fun dataAdded() {
        val title = R.string.data_added
        val positiveButton = "تمام"

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#59A5E1'>$positiveButton</font>")) { _, _ -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        //setText Empty
        binding.name.setText("")
        binding.address.setText("")
        binding.description.setText("")
        binding.phoneNumber.setText("")
    }

    fun noInternet() {
        val title = R.string.no_internet
        val positiveButton = "تمام"

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setIcon(R.drawable.cloud)
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#59A5E1'>$positiveButton</font>")) { _, _ -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}