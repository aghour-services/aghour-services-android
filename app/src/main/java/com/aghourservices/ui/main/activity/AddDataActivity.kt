package com.aghourservices.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.data.model.Firm
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.ActivityAddDataBinding
import com.aghourservices.ui.adapter.SpinnerCategoriesAdapter
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.main.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.showProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog.Companion.createAccount
import com.aghourservices.utils.interfaces.AlertDialog.Companion.dataAdded
import com.aghourservices.utils.interfaces.AlertDialog.Companion.noInternet
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    private lateinit var categoryList: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.add_data_fragment)
        hideUserAddData()
        loadCategories()
        spinnerAdapter()
        initUserClicks()
    }

    private fun initUserClicks() {
        binding.apply {

            btnRegister.setOnClickListener {
                createAccount(this@AddDataActivity)
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
        showProgressDialog(this)
        val user = getUserData(this)
        val retrofitBuilder = RetrofitInstance(this).firmsApi.createFirm(
            firm.toJsonObject(),
            user.token
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
                hideProgressDialog()
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
        binding.name.text.clear()
        binding.address.text.clear()
        binding.description.text.clear()
        binding.phoneNumber.text.clear()
        hideProgressDialog()
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