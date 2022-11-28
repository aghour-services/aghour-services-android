package com.aghourservices.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Category
import com.aghourservices.databinding.FragmentCategoriesBinding
import com.aghourservices.ui.adapter.CategoriesAdapter
import com.aghourservices.ui.factory.CategoriesVIewModelFactory
import com.aghourservices.ui.viewModel.CategoriesViewModel

class CategoriesFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private val categoriesViewModel: CategoriesViewModel by viewModels { CategoriesVIewModelFactory() }
    private val categoryAdapter = CategoriesAdapter { position -> onListItemClick(position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)
        initRecyclerView()
        setUpViewModel()
        return binding.root
    }

    private fun setUpViewModel() {
        activity?.let { categoriesViewModel.loadCategories(it) }
        categoriesViewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            categoryAdapter.setCategories(it)
            progressBar()
        }
    }

    private fun initRecyclerView() {
        requireActivity().title = getString(R.string.categories_fragment)
        binding.categoriesRecyclerview.apply {
            setHasFixedSize(true)
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun onListItemClick(position: Int) {
        val categoryId = categoryAdapter.getCategory(position).id
        val categoryName = categoryAdapter.getCategory(position).name
        val firmsFragment = CategoriesFragmentDirections.actionCategoriesFragmentToFirmsFragment(
            categoryId, categoryName
        )
        findNavController().navigate(firmsFragment)
    }

    private fun progressBar() {
        binding.progressBar.visibility = View.GONE
        binding.categoriesRecyclerview.visibility = View.VISIBLE
    }
}