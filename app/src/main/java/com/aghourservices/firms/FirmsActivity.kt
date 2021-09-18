package com.aghourservices.firms

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aghourservices.BuildConfig.DEBUG
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.firms.api.ApiServices
import com.aghourservices.firms.api.FirmItem
import com.aghourservices.firms.ui.FirmsAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
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

    //define SwipeRefreshLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firms)
        initViews()
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        firmsRecyclerView.setHasFixedSize(true)
        firmsRecyclerView.layoutManager = LinearLayoutManager(this)

        val categoryId = intent.getIntExtra("category_id", 0)
        val categoryName = intent.getStringExtra("category_name")
        toolBarTv.text = categoryName

        loadFirms(categoryId)

        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
                loadFirms(categoryId)
            }, 1000)
        }

        AghourAdManager.displayBannerAd(this, adView)
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
                adapter = FirmsAdapter(applicationContext, responseBody) { position ->
                    onListItemClick(position)
                }
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}