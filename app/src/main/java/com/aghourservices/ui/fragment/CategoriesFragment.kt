package com.aghourservices.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.databinding.FragmentCategoriesBinding
import com.aghourservices.ui.adapter.CategoriesAdapter
import com.aghourservices.ui.viewModel.CategoriesViewModel

class CategoriesFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var categoryAdapter: CategoriesAdapter
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var categoryList: ArrayList<Category>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)
        setUpViewModel()
        initRecyclerView()
        return binding.root
    }

    private fun setUpViewModel() {
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        categoriesViewModel.loadCategories(activity!!)
        categoriesViewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            categoryList = it
            categoryAdapter = CategoriesAdapter(it) { position -> onListItemClick(position) }
            categoryAdapter.setData(it)
            binding.categoriesRecyclerview.adapter = categoryAdapter
            progressBar()
        }
    }

    private fun initRecyclerView() {
        requireActivity().title = getString(R.string.categories_fragment)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.categoriesRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }


    private fun onListItemClick(position: Int) {
        val categoryId = categoryList[position].id
        val categoryName = categoryList[position].name
        val firmsFragment =
            CategoriesFragmentDirections.actionCategoriesFragmentToFirmsFragment(
                categoryId,
                categoryName
            )
        findNavController().navigate(firmsFragment)
    }

    private fun progressBar() {
        binding.progressBar.visibility = View.GONE
        binding.categoriesRecyclerview.visibility = View.VISIBLE
    }
}