package com.aghourservices.firms

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.api.Category
import com.aghourservices.constants.RetrofitInstance
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

class AddDataFragment : BaseFragment() {
    private lateinit var binding: FragmentAddDataBinding
    private lateinit var categoryList: ArrayList<Category>
    private var progressDialog: Dialog? = null

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
        showBottomNav()
        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.show()
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
        val spinnerAdapter = SpinnerAdapter(requireActivity(), Category.Categories.List!!)
        val spinner = binding.spinner
        spinner.adapter = spinnerAdapter
    }

    private fun createFirm(firm: Firm) {
        val user = UserInfo().getUserData(requireActivity())
        val retrofitBuilder = RetrofitInstance(requireActivity()).retrofit.create(CreateFirm::class.java)
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
        binding.name.text.clear()
        binding.address.text.clear()
        binding.description.text.clear()
        binding.phoneNumber.text.clear()
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