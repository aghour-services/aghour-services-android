package com.aghourservices.search

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.ads.Interstitial
import com.aghourservices.databinding.ActivitySearchBinding
import com.aghourservices.firebase_analytics.Event
import com.aghourservices.search.api.ApiServices
import com.aghourservices.search.api.SearchResult
import com.aghourservices.search.ui.SearchResultAdapter
import com.google.android.gms.ads.AdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class SearchActivity : AppCompatActivity() {
    private lateinit var searchResults: ArrayList<SearchResult>
    private lateinit var adapter: SearchResultAdapter
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchResultRecycler.setHasFixedSize(true)
        binding.searchResultRecycler.layoutManager = LinearLayoutManager(this)

        adView = findViewById(R.id.adView)
        Banner.show(this, adView)

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
            finish()
        }
    }

    private fun search(text: String) {
        val eventName = "search_${text}"
        Event.sendFirebaseEvent(eventName, text)
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)
        val retrofitData = retrofitBuilder.search(text)


        retrofitData.enqueue(object : Callback<ArrayList<SearchResult>?> {
            @SuppressLint("NotifyDataSetChanged")
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
        if (searchResults.isEmpty()) {
            binding.searchResultRecycler.visibility = View.GONE
            return
        }
        binding.searchResultRecycler.visibility = View.VISIBLE
        adapter = SearchResultAdapter(applicationContext, searchResults) { position ->
            onListItemClick(position)
        }
        binding.searchResultRecycler.adapter = adapter
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