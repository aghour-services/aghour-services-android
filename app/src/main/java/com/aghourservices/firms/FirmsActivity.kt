package com.aghourservices.firms

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.firms.api.ApiServices
import com.aghourservices.firms.api.Firm
import com.aghourservices.firms.ui.FirmsAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://aghour-services.magdi.work/api/"

class FirmsActivity : AppCompatActivity() {

    lateinit var adapter: FirmsAdapter
    private lateinit var toolBar: Toolbar
    private lateinit var toolBarTv: TextView
    private lateinit var firmsRecyclerView: RecyclerView
    private lateinit var firmsList: ArrayList<Firm>
    private lateinit var adView: AdView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var realm: Realm
    private lateinit var firmsShimmer: ShimmerFrameLayout
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    //define SwipeRefreshLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firms)
        initViews()

        Realm.init(this)
        val config = RealmConfiguration
            .Builder()
            .allowWritesOnUiThread(true)
            .name("firm.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.getInstance(config)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        firmsRecyclerView.setHasFixedSize(true)
        firmsRecyclerView.layoutManager = LinearLayoutManager(this)

        val categoryId = intent.getIntExtra("category_id", 0)
        val categoryName = intent.getStringExtra("category_name")
        toolBarTv.text = categoryName

        runnable = Runnable { loadFirms(categoryId) }
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1000)

        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
                loadFirms(categoryId)
            }, 1200)
        }

        AghourAdManager.displayBannerAd(this, adView)
    }

    private fun loadFirms(categoryId: Int) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val retrofitData = retrofitBuilder.loadFirms(categoryId)

        retrofitData.enqueue(object : Callback<ArrayList<Firm>?> {

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<Firm>?>,
                response: Response<ArrayList<Firm>?>,
            ) {
                firmsList = response.body()!!
                realm.executeTransaction {
                    for (i in firmsList) {
                        try {
                            val firm = realm.createObject(Firm::class.java, i.id)
                            firm.name = i.name
                            firm.address = i.address
                            firm.description = i.description
                            firm.phone_number = i.phone_number
                            firm.category_id = i.category_id
                        } catch (e: Exception) {
                        }
                    }
                }
                setAdapter(firmsList)
                stopShimmerAnimation()
            }

            override fun onFailure(call: Call<ArrayList<Firm>?>, t: Throwable) {
                val result =
                    realm.where(Firm::class.java).equalTo("category_id", categoryId).findAll()
                firmsList = ArrayList<Firm>()
                firmsList.addAll(result)
                setAdapter(firmsList)

                Toast.makeText(this@FirmsActivity, "لا يوجد انترنت", Toast.LENGTH_SHORT).show()
                stopShimmerAnimation()
            }
        })
    }

    private fun stopShimmerAnimation() {
        firmsShimmer.stopShimmer()
        firmsShimmer.visibility = View.GONE
        firmsRecyclerView.visibility = View.VISIBLE
    }


    private fun setAdapter(firmsList: ArrayList<Firm>) {
        adapter = FirmsAdapter(applicationContext, firmsList) { position ->
            onListItemClick(position)
        }
        firmsRecyclerView.adapter = adapter
    }

    private fun onListItemClick(position: Int) {
        val phoneNumber = firmsList[position].phone_number
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
        toolBar = findViewById(R.id.toolbar)
        toolBarTv = findViewById(R.id.toolBarTv)
        firmsRecyclerView = findViewById(R.id.firmsRecyclerview)
        adView = findViewById(R.id.adView)
        firmsShimmer = findViewById(R.id.firmsShimmer)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}