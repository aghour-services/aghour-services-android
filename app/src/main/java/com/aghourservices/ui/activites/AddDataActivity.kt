package com.aghourservices.ui.activites

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.data.model.Firm
import com.aghourservices.data.network.RetrofitInstance.firmsApi
import com.aghourservices.databinding.ActivityAddDataBinding
import com.aghourservices.ui.viewHolders.SpinnerCategoriesAdapter
import com.aghourservices.utils.services.cache.UserInfo
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.aghourservices.utils.services.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.ads.Banner
import com.aghourservices.utils.helper.ProgressDialog
import com.aghourservices.utils.helper.AlertDialogs.Companion.createAccount
import com.aghourservices.utils.helper.AlertDialogs.Companion.dataAdded
import com.aghourservices.utils.helper.AlertDialogs.Companion.noInternet
import com.google.android.gms.ads.AdView
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var adView: AdView
    private val progressDialog by lazy { ProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.add_data_fragment)
        hideUserAddData()
        loadCategories()
        spinnerAdapter()
        initUserClicks()
        adView()
    }

    private fun adView() {
        adView = findViewById(R.id.adView)
        Banner.show(this, adView)
    }

    private fun initUserClicks() {
        binding.apply {

            backBtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnRegister.setOnClickListener {
                createAccount(this@AddDataActivity, "لإضافة بيانات أعمل حساب الأول")
            }

            btnAddData.setOnClickListener(View.OnClickListener {
                val name = binding.name.text.toString()
                val address = binding.address.text.toString()
                val description = binding.description.text.toString()
                val phoneNumber = binding.phoneNumber.text.toString()
                val selectedCategory = categoryList[spinner.selectedItemPosition]

                val firm = Firm().apply {
                    this.name = name
                    this.address = address
                    this.description = description
                    this.phone_number = phoneNumber
                    this.category_id = selectedCategory.id
                }

                if (firm.inValid()) {
                    binding.name.error = "الاسم"
                    binding.address.error = "العنوان"
                    binding.phoneNumber.error = "اكتب رقم التليفون"
                    binding.description.error = "اكتب وصف عن صاحب المكان"
                    return@OnClickListener
                } else {
                    createFirm(firm)
                }
            })
        }
    }

    private fun spinnerAdapter() {
        val spinnerCategoriesAdapter = SpinnerCategoriesAdapter(this, categoryList)
        val spinner = binding.spinner
        spinner.adapter = spinnerCategoriesAdapter
    }

    private fun createFirm(firm: Firm) {
        progressDialog.show(getString(R.string.adding_data))
        val user = getUserData(this)
        val retrofitBuilder = firmsApi.createFirm(
            firm.toJsonObject(),
            user.token,
            UserInfo.getFCMToken(this)
        )
        retrofitBuilder.enqueue(object : Callback<Firm> {
            override fun onResponse(call: Call<Firm>, response: Response<Firm>) {
                if (response.isSuccessful) {
                    dataAdded(this@AddDataActivity)
                    setTextEmpty()
                }
            }

            override fun onFailure(call: Call<Firm>, t: Throwable) {
                noInternet(this@AddDataActivity)
                progressDialog.hide()
            }
        })
    }

    private fun loadCategories() {
        val realm = Realm.getDefaultInstance()
        val result = realm.where(Category::class.java).findAll()
        categoryList = ArrayList()
        categoryList.addAll(result)
    }

    private fun setTextEmpty() {
        binding.apply {
            name.text?.clear()
            address.text?.clear()
            description.text?.clear()
            phoneNumber.text?.clear()
        }
        progressDialog.hide()
    }

    private fun hideUserAddData() {
        val isUserLogin = isUserLoggedIn(this)
        if (isUserLogin) {
            binding.btnAddData.visibility = View.VISIBLE
        } else {
            binding.btnAddData.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }
}