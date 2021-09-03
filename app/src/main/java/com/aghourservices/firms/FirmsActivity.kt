package com.aghourservices.firms

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.firms.api.ApiServices
import com.aghourservices.firms.api.FirmItem
import com.aghourservices.firms.ui.FirmsAdapter
import com.google.android.gms.ads.AdView
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

class FirmsActivity : AppCompatActivity() {

    lateinit var adapter: FirmsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var toolBar: Toolbar
    private lateinit var toolBarTv: TextView
    private lateinit var firmsRecyclerView: RecyclerView
    private lateinit var firmsList: ArrayList<FirmItem>
    private lateinit var adView: AdView

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firms)
        initViews()
        setSupportActionBar(toolBar)
        AghourAdManager.loadAd(this, adView)

        firmsRecyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        firmsRecyclerView.layoutManager = linearLayoutManager
        firmsRecyclerView.layoutManager = GridLayoutManager(this, 1)

        var categoryId = intent.getIntExtra("category_id", 0)
        var categoryName = intent.getStringExtra("category_name")

        toolBarTv.text = categoryName
        loadFirms(categoryId)
    }

    private fun loadFirms(categoryId: Int) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val firmsList = retrofitBuilder.loadFirms(categoryId)

        firmsList.enqueue(object : Callback<ArrayList<FirmItem>?> {

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<FirmItem>?>,
                response: Response<ArrayList<FirmItem>?>,
            ) {
                val responseBody = response.body()!!
                this@FirmsActivity.firmsList = responseBody
                adapter = FirmsAdapter(responseBody) { position -> onListItemClick(position) }
                firmsRecyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<FirmItem>?>, t: Throwable) {
                Log.d("FirmsActivity", "onFailure: " + t.message)
            }
        })
    }

    private fun onListItemClick(position: Int) {
        val phoneNumber = firmsList.get(position).phone_number
        sendFirebaseEvent("Call", phoneNumber)
        callPhone(phoneNumber)
    }

    private fun sendFirebaseEvent(eventName: String, data: String) {
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(eventName) {
            param("data", data)
        }
    }

    private fun callPhone(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    private fun initViews() {
        toolBar = findViewById(R.id.toolBar)
        toolBarTv = findViewById(R.id.toolBarTv)
        firmsRecyclerView = findViewById(R.id.firmsRecyclerview)
        adView = findViewById(R.id.adView)
    }
}