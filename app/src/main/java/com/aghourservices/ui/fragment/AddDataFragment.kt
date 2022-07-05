package com.aghourservices.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.aghourservices.R
import com.aghourservices.data.db.RealmConfiguration
import com.aghourservices.data.model.Category
import com.aghourservices.data.model.Firm
import com.aghourservices.databinding.FragmentAddDataBinding
import com.aghourservices.ui.adapter.SpinnerCategoriesAdapter
import com.aghourservices.ui.main.cache.UserInfo.isUserLoggedIn
import com.aghourservices.ui.viewModel.AddDataViewModel
import com.aghourservices.utils.interfaces.AlertDialog

class AddDataFragment : BaseFragment() {
    private lateinit var binding: FragmentAddDataBinding
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var addDataViewModel: AddDataViewModel
    private val firm: Firm = Firm()

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
        initUserClick()
        loadCategories()
        spinnerAdapter()
        setUpViewModel()
    }

    private fun setUpViewModel() {
        addDataViewModel = ViewModelProvider(this)[AddDataViewModel::class.java]
    }

    private fun initUserClick() {
        binding.apply {
            btnRegister.setOnClickListener {
                AlertDialog.createAccount(requireActivity())
            }

            btnAddData.setOnClickListener(View.OnClickListener {
                val selectedCategoryPosition = binding.spinner.selectedItemPosition
                val selectedCategory = categoryList[selectedCategoryPosition]
                val name = binding.name.text.toString()
                val address = binding.address.text.toString()
                val description = binding.description.text.toString()
                val phoneNumber = binding.phoneNumber.text.toString()

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
                    activity?.let { it1 -> addDataViewModel.createFirm(it1, firm) }
                }
            })
        }
    }

    private fun spinnerAdapter() {
        val spinnerCategoriesAdapter = SpinnerCategoriesAdapter(requireActivity(), categoryList)
        val spinner = binding.spinner
        spinner.adapter = spinnerCategoriesAdapter
    }

    private fun loadCategories() {
        val realm = RealmConfiguration(requireContext()).realm
        val result = realm.where(Category::class.java).findAll()
        categoryList = ArrayList()
        categoryList.addAll(result)
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