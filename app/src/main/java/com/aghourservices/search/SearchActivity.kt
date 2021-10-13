package com.aghourservices.search

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.search.api.ApiServices
import com.aghourservices.search.api.SearchResult
import com.aghourservices.search.ui.SearchResultAdapter
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://aghour-services.magdi.work/api/"

class SearchActivity : AppCompatActivity() {
    private lateinit var searchImageIc: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var searchResultRecycler: RecyclerView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var searchResults: ArrayList<SearchResult>
    lateinit var adapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()


        searchResultRecycler.setHasFixedSize(true)
        searchResultRecycler.layoutManager = LinearLayoutManager(this)

        searchImageIc.setOnClickListener {
            search(searchEditText.text.toString())
        }

        searchEditText.doOnTextChanged { text, start, before, count ->
            search(text.toString())
        }
    }


    private fun search(text: String) {
        sendFirebaseEvent("Search", text)
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
                stopShimmerAnimation()
            }

            override fun onFailure(call: Call<ArrayList<SearchResult>?>, t: Throwable) {

            }
        })
    }


    private fun stopShimmerAnimation() {
//        firmsShimmer.stopShimmer()
//        firmsShimmer.visibility = View.GONE
        searchResultRecycler.visibility = View.VISIBLE
    }


    private fun setAdapter(searchResults: ArrayList<SearchResult>) {
        adapter = SearchResultAdapter(applicationContext, searchResults) { position ->
            onListItemClick(position)
        }
        searchResultRecycler.adapter = adapter
    }

    private fun onListItemClick(position: Int) {
        val phoneNumber = searchResults[position].phone_number
        sendFirebaseEvent("Call", phoneNumber)
        callPhone(phoneNumber)
    }

    private fun callPhone(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    private fun sendFirebaseEvent(eventName: String, data: String) {
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(eventName) {
            param("data", data)
        }
    }

    private fun initViews() {
        searchImageIc = findViewById(R.id.search_image_view)
        searchEditText = findViewById(R.id.search_text)
        searchResultRecycler = findViewById(R.id.searchResultRecycler)
    }
}