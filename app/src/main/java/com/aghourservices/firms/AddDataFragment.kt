package com.aghourservices.firms

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.api.Category
import com.aghourservices.databinding.FragmentAddDataBinding
import com.aghourservices.firms.api.CreateFirm
import com.aghourservices.interfaces.AlertDialog.Companion.dataAdded
import com.aghourservices.interfaces.AlertDialog.Companion.noInternet
import com.aghourservices.user.SignUpActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class AddDataFragment : BaseFragment() {
    private lateinit var binding: FragmentAddDataBinding
    private lateinit var categoryList: ArrayList<Category>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDataBinding.inflate(layoutInflater)
        hideUserAddData()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.show()

        val bundle = arguments
        if (bundle != null) {
            val value1 = bundle.getInt("category_id", -1)
            val value2 = bundle.getString("category_name", "")
            Toast.makeText(requireActivity(), value1.toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(requireActivity(), value2, Toast.LENGTH_SHORT).show()
        }
        init()

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(requireActivity(), SignUpActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun init() {
        loadCategories()
        spinnerAdapter(categoryList)
        requireActivity().title = getString(R.string.add_data_fragment)
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
                binding.phoneNumber.error = "اكتب رقم التليفون"
                binding.description.error = "اكتب وصف عن صاحب المكان"

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
            ArrayAdapter(
                requireActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                categories
            )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinner)
        {
            adapter = spinnerAdapter
        }
    }

    private fun createFirm(firm: Firm) {
        val user = UserInfo().getUserData(requireActivity())
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build().create(CreateFirm::class.java)
        val retrofitData = retrofitBuilder.createFirm(firm.toJsonObject(), user.token)
        retrofitData.enqueue(object : Callback<Firm> {
            override fun onResponse(call: Call<Firm>, response: Response<Firm>) {
                dataAdded(requireContext())
                setTextEmpty()
            }

            override fun onFailure(call: Call<Firm>, t: Throwable) {
                noInternet(requireContext())
            }
        })
    }

    private fun loadCategories() {
        Realm.init(requireContext())
        val config = RealmConfiguration.Builder().name("category.realm")
            .deleteRealmIfMigrationNeeded().schemaVersion(1)
            .allowWritesOnUiThread(true).build()
        val categoryRealm = Realm.getInstance(config)
        val result = categoryRealm.where(Category::class.java).findAll()
        categoryList = ArrayList()
        categoryList.addAll(result)
    }

    private fun setTextEmpty() {
        binding.name.setText("")
        binding.address.setText("")
        binding.description.setText("")
        binding.phoneNumber.setText("")
    }

    private fun hideUserAddData() {
        val isUserLogin = UserInfo().isUserLoggedIn(requireActivity())
        if (isUserLogin) {
            binding.btnAddData.visibility = View.VISIBLE
        } else {
            binding.btnAddData.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }
}