package com.aghourservices.firms

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.firms.api.ApiServices
import com.aghourservices.firms.api.FirmItem
import com.aghourservices.firms.ui.FirmsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://aghour-services-backend.herokuapp.com/api/"

class MarketsActivity : AppCompatActivity() {

    lateinit var adapter: FirmsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var toolBar: Toolbar
    private lateinit var firmsRecyclerView: RecyclerView
    private lateinit var firmsList: ArrayList<FirmItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firms_view)
        initViews()
        setSupportActionBar(toolBar)

        firmsRecyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        firmsRecyclerView.layoutManager = linearLayoutManager
        firmsRecyclerView.layoutManager = GridLayoutManager(this, 1)

        var categoryId = intent.getIntExtra("category_id", 0)
        loadMarketsList(categoryId)
    }

    private fun loadMarketsList(categoryId: Int) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val dataMarkets = retrofitBuilder.loadMarketsList(categoryId)

        dataMarkets.enqueue(object : Callback<ArrayList<FirmItem>?> {

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<FirmItem>?>,
                response: Response<ArrayList<FirmItem>?>,
            ) {
                val responseBody = response.body()!!
                firmsList = responseBody
                adapter = FirmsAdapter(responseBody) { position -> onListItemClick(position) }
                firmsRecyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<FirmItem>?>, t: Throwable) {
                Log.d("MarketsActivity", "onFailure: " + t.message)
            }
        })
    }

    private fun onListItemClick(position: Int) {
        val phoneNumber = firmsList.get(position).phone_number
        callPhone(phoneNumber)
    }

    private fun callPhone(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    private fun initViews() {
        toolBar = findViewById(R.id.toolBar)
        firmsRecyclerView = findViewById(R.id.firmsRecyclerview)
    }
}