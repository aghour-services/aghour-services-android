package com.aghourservices.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.data.model.Search
import com.aghourservices.databinding.FragmentSearchBinding
import com.aghourservices.ui.adapters.SearchResultAdapter
import com.aghourservices.ui.viewModels.SearchViewModel
import com.aghourservices.utils.helper.CheckNetworkLiveData
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.helper.Intents.showKeyboard
import com.aghourservices.utils.services.cache.UserInfo

class SearchFragment : BaseFragment() {
    private lateinit var searchList: ArrayList<Search>
    private lateinit var searchAdapter: SearchResultAdapter
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetwork()
        hideBottomNavigation()
        hideToolbar()
        initRecyclerView()
        setUpViewModel()
        setSearchText()
    }

    private fun setSearchText() {
        val searchText = binding.searchText.text.toString()
        showKeyboard(requireActivity(), binding.searchText)

        binding.searchText.setOnClickListener {
            activity?.let {
                searchViewModel.search(
                searchText, UserInfo.getFCMToken(requireContext())
            ) }
        }

        binding.searchText.doOnTextChanged { text, _, _, _ ->
            val searchKeyWord = text.toString()
            if (searchKeyWord.length > 2) {
                activity?.let { searchViewModel.search(
                    searchKeyWord, UserInfo.getFCMToken(requireContext())
                ) }
            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            searchResultRecycler.setHasFixedSize(true)
            searchResultRecycler.layoutManager = LinearLayoutManager(activity)
            backBtn.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun setUpViewModel() {
        val searchText = binding.searchText.text.toString()

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        activity?.let { searchViewModel.search(
            searchText, UserInfo.getFCMToken(requireContext())
        ) }

        searchViewModel.searchLiveData.observe(viewLifecycleOwner) {
            searchList = it
            searchAdapter = SearchResultAdapter(requireContext(), searchList) { position ->
                onListItemClick(position)
            }
            binding.apply {
                searchResultRecycler.isVisible = true
                searchResultRecycler.adapter = searchAdapter
            }

            if (it.isEmpty()) {
                binding.searchResultRecycler.isVisible = false
                return@observe
            }
        }
    }

    private fun checkNetwork() {
        val checkNetworkLiveData = activity?.application?.let { CheckNetworkLiveData(it) }
        checkNetworkLiveData?.observe(viewLifecycleOwner) { isConnected ->
            binding.lottieAnimationView.isVisible = isConnected
            binding.noInternet.isVisible = !isConnected
        }
    }

    private fun onListItemClick(position: Int) {
        val firm = searchList[position]
        val phoneNumber = firm.phone_number
        val eventName = "call_${firm.name}"
        Event.sendFirebaseEvent(eventName, phoneNumber)
        callPhone(phoneNumber)
    }

    private fun callPhone(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigation()
        showToolbar()
    }
}