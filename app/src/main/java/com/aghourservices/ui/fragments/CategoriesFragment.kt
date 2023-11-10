package com.aghourservices.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Device
import com.aghourservices.databinding.FragmentCategoriesBinding
import com.aghourservices.ui.adapters.CategoriesAdapter
import com.aghourservices.ui.viewModels.CategoriesViewModel
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken

class CategoriesFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private val categoryAdapter = CategoriesAdapter { position -> onListItemClick(position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)
        initRecyclerView()
        initCategoryObserve()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendDevice(getFCMToken(requireContext()))
        showBottomNavigation()
        showToolbar()
    }

    private fun initCategoryObserve() {
        activity?.let { categoriesViewModel.loadCategories(getFCMToken(requireContext())) }
        categoriesViewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            categoryAdapter.setCategories(it)
            progressBar()
        }
    }

    private fun sendDevice(token: String) {
        val device = Device(token)
        categoriesViewModel.sendDevice(device, token)
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