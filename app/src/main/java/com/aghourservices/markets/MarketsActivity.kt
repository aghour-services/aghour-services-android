package com.aghourservices.markets

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.markets.marketsApi.ApiServices
import com.aghourservices.markets.marketsApi.MarketItem
import com.aghourservices.markets.ui.MarketsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_markets_view.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://jsonplaceholder.typicode.com"
class MarketsActivity : AppCompatActivity() {

    lateinit var adapter: MarketsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markets_view)
        setSupportActionBar(Toolbar)

        marketList.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        marketList.layoutManager = linearLayoutManager
        loadMarketsList()
        marketList.layoutManager = GridLayoutManager(this, 1)

    }

    private fun loadMarketsList() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val dataMarkets = retrofitBuilder.loadMarketsList()

        dataMarkets.enqueue(object : Callback<ArrayList<MarketItem>?>{

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<MarketItem>?>,
                response: Response<ArrayList<MarketItem>?>
            ) {
                val responseBody = response.body()!!
                Log.v("DATA", responseBody.toString())
                adapter = MarketsAdapter(responseBody)
                marketList.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<MarketItem>?>, t: Throwable) {
                Log.d("MarketsActivity", "onFailure: " + t.message)
            }
        })
    }
}