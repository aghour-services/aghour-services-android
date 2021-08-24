package com.aghourservices.markets

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.markets.api.ApiServices
import com.aghourservices.markets.api.MarketItem
import com.aghourservices.markets.ui.MarketsAdapter
import kotlinx.android.synthetic.main.activity_markets_view.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.FieldPosition

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

        dataMarkets.enqueue(object : Callback<ArrayList<MarketItem>?> {

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<MarketItem>?>,
                response: Response<ArrayList<MarketItem>?>
            ) {
                val responseBody = response.body()!!
                Log.v("DATA", responseBody.toString())
                adapter = MarketsAdapter(responseBody) { position -> onListItemClick(position) }
                marketList.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<MarketItem>?>, t: Throwable) {
                Log.d("MarketsActivity", "onFailure: " + t.message)
            }
        })
    }

    private fun onListItemClick(position: Int) {
        val phoneNumber = "01287303441"
        callPhone(phoneNumber)
    }
    private fun callPhone(phoneNumber: String) {
        Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show()
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }
}