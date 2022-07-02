package com.aghourservices.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.data.model.Search
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentSearchBinding
import com.aghourservices.ui.adapter.SearchResultAdapter
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : BaseFragment() {
    private lateinit var searches: ArrayList<Search>
    private lateinit var adapter: SearchResultAdapter
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        hideToolbar()

        if (binding.searchText.requestFocus()) {
            ShowSoftKeyboard.show(requireActivity(), binding.searchText)
        }

        binding.searchResultRecycler.setHasFixedSize(true)
        binding.searchResultRecycler.layoutManager = LinearLayoutManager(requireActivity())

        binding.searchText.setOnClickListener {
            search(binding.searchText.text.toString())
        }

        binding.searchText.doOnTextChanged { text, _, _, _ ->
            val searchKeyWord = text.toString()
            if (searchKeyWord.length > 2) {
                search(searchKeyWord)
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigation()
        showToolbar()
    }

    private fun search(text: String) {
        val eventName = "search_${text}"
        Event.sendFirebaseEvent(eventName, text)
        val retrofitBuilder = activity?.let {
            RetrofitInstance(it).searchApi.search(text)
        }

        retrofitBuilder?.enqueue(object : Callback<ArrayList<Search>?> {
            override fun onResponse(
                call: Call<ArrayList<Search>?>,
                response: Response<ArrayList<Search>?>,
            ) {
                searches = response.body()!!
                setAdapter(searches)
            }

            override fun onFailure(call: Call<ArrayList<Search>?>, t: Throwable) {
                customView()
            }
        })
    }

    private fun setAdapter(searches: ArrayList<Search>) {
        try {
            if (searches.isEmpty()) {
                binding.searchResultRecycler.visibility = View.GONE
                return
            }
            binding.searchResultRecycler.visibility = View.VISIBLE
            adapter = SearchResultAdapter(requireContext(), searches) { position ->
                onListItemClick(position)
            }
            binding.searchResultRecycler.adapter = adapter
        } catch (e: Exception) {
        }
    }

    private fun onListItemClick(position: Int) {
        val firm = searches[position]
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

    fun customView() {
        binding.lottieAnimationView.visibility = View.GONE
        binding.noInternet.visibility = View.VISIBLE
    }
}