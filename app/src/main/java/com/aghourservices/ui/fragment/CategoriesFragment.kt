package com.aghourservices.ui.fragment

import android.annotation.SuppressLint
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
import com.aghourservices.ui.adapter.CategoriesAdapter
import com.aghourservices.ui.viewModel.CategoriesViewModel
import com.aghourservices.utils.helper.Intents.getDeviceId
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class CategoriesFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private val categoryAdapter = CategoriesAdapter { position -> onListItemClick(position) }
    private val deviceId: String by lazy { getDeviceId(requireContext()) }

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
        getFirebaseInstanceToken()
    }

    private fun initCategoryObserve() {
        activity?.let { categoriesViewModel.loadCategories(it, deviceId) }
        categoriesViewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            categoryAdapter.setCategories(it)
            progressBar()
        }
    }

    @SuppressLint("HardwareIds")
    private fun sendDevice(token: String) {
        val device = Device(deviceId, token)
        categoriesViewModel.sendDevice(requireContext(), device, deviceId)
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

    private fun getFirebaseInstanceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            sendDevice(token)
        })
    }
}