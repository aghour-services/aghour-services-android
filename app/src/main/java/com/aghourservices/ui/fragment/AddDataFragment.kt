package com.aghourservices.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.data.model.Firm
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentAddDataBinding
import com.aghourservices.ui.adapter.SpinnerCategoriesAdapter
import com.aghourservices.ui.main.activity.SignUpActivity
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.main.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.showProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog.Companion.dataAdded
import com.aghourservices.utils.interfaces.AlertDialog.Companion.noInternet
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDataFragment : BaseFragment() {
    private lateinit var binding: FragmentAddDataBinding
    private lateinit var categoryList: ArrayList<Category>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.add_data_fragment)
        hideUserAddData()
        init()

        val bundle = arguments
        if (bundle != null) {
            val value1 = bundle.getInt("category_id", -1)
            val value2 = bundle.getString("category_name", "")
            Toast.makeText(requireActivity(), value1.toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(requireActivity(), value2, Toast.LENGTH_SHORT).show()
        }

        binding.btnRegister.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.create_account_first))
            alertDialogBuilder.setMessage(getString(R.string.should_create))
            alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton("إنشاء الان") { _, _ ->
                startActivity(Intent(requireActivity(), SignUpActivity::class.java))
                requireActivity().finish()
            }
            alertDialogBuilder.setNegativeButton(R.string.cancelButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 18f
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 18f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextAppearance(R.style.SegoeTextBold)
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextAppearance(R.style.SegoeTextBold)
            }
        }
    }

    private fun init() {
        loadCategories()
        spinnerAdapter()

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

    private fun spinnerAdapter() {
        val spinnerCategoriesAdapter = SpinnerCategoriesAdapter(requireActivity(), categoryList)
        val spinner = binding.spinner
        spinner.adapter = spinnerCategoriesAdapter
    }

    private fun createFirm(firm: Firm) {
        showProgressDialog(requireContext())
        val user = getUserData(requireActivity())
        val retrofitBuilder =
            activity?.let {
                RetrofitInstance(it).firmsApi.createFirm(
                    firm.toJsonObject(),
                    user.token
                )
            }
        retrofitBuilder?.enqueue(object : Callback<Firm> {
            override fun onResponse(call: Call<Firm>, response: Response<Firm>) {
                dataAdded(requireContext())
                hideProgressDialog()
                setTextEmpty()
            }

            override fun onFailure(call: Call<Firm>, t: Throwable) {
                noInternet(requireContext())
                hideProgressDialog()
            }
        })
    }

    private fun loadCategories() {
        Realm.init(requireContext())
        val config = RealmConfiguration.Builder().name("offline.realm")
            .deleteRealmIfMigrationNeeded().schemaVersion(1)
            .allowWritesOnUiThread(true).build()
        val categoryRealm = Realm.getInstance(config)
        val result = categoryRealm.where(Category::class.java).findAll()
        categoryList = ArrayList()
        categoryList.addAll(result)
    }

    private fun setTextEmpty() {
        binding.name.text.clear()
        binding.address.text.clear()
        binding.description.text.clear()
        binding.phoneNumber.text.clear()
    }

    private fun hideUserAddData() {
        val isUserLogin = isUserLoggedIn(requireActivity())
        if (isUserLogin) {
            binding.btnAddData.visibility = View.VISIBLE
        } else {
            binding.btnAddData.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }
}