package com.aghourservices.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.BaseFragment
import com.aghourservices.constants.RetrofitInstance
import com.aghourservices.databinding.FragmentSearchBinding
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.interfaces.ShowSoftKeyboard
import com.aghourservices.search.api.ApiServices
import com.aghourservices.search.api.SearchResult
import com.aghourservices.search.ui.SearchResultAdapter
import com.google.android.gms.ads.AdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : BaseFragment() {
    private lateinit var searchResults: ArrayList<SearchResult>
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
        hideBottomNav()

        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.hide()

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

    private fun search(text: String) {
        val eventName = "search_${text}"
        Event.sendFirebaseEvent(eventName, text)
        val retrofitBuilder =
            activity?.let { RetrofitInstance(it).retrofit.create(ApiServices::class.java) }
        val retrofitData = retrofitBuilder?.search(text)
        retrofitData?.enqueue(object : Callback<ArrayList<SearchResult>?> {
            override fun onResponse(
                call: Call<ArrayList<SearchResult>?>,
                response: Response<ArrayList<SearchResult>?>,
            ) {
                searchResults = response.body()!!
                setAdapter(searchResults)
            }

            override fun onFailure(call: Call<ArrayList<SearchResult>?>, t: Throwable) {
                customView()
            }
        })
    }

    private fun setAdapter(searchResults: ArrayList<SearchResult>) {
        try {
            if (searchResults.isEmpty()) {
                binding.searchResultRecycler.visibility = View.GONE
                return
            }
            binding.searchResultRecycler.visibility = View.VISIBLE
            adapter = SearchResultAdapter(requireContext(), searchResults) { position ->
                onListItemClick(position)
            }
            binding.searchResultRecycler.adapter = adapter
        } catch (e: Exception) {
        }
    }

    private fun onListItemClick(position: Int) {
        val firm = searchResults[position]
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