package com.aghourservices.search

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
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
    private lateinit var searchToolbar: Toolbar
    private lateinit var backButton: ImageView
    private lateinit var searchEditText: AppCompatEditText
    private lateinit var noDataTv: TextView
    private lateinit var searchResultRecycler: RecyclerView
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var searchResults: ArrayList<SearchResult>
    private lateinit var adapter: SearchResultAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()


        setSupportActionBar(searchToolbar)


        searchResultRecycler.setHasFixedSize(true)
        searchResultRecycler.layoutManager = LinearLayoutManager(this)

        searchEditText.setOnClickListener {
            search(searchEditText.text.toString())
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            search(text.toString())
        }

        backButton.setOnClickListener {
            val AlertDialogBuilder = AlertDialog.Builder(this)
            AlertDialogBuilder.setTitle("تحذير...")
            AlertDialogBuilder.setIcon(R.mipmap.ic_launcher)
            AlertDialogBuilder.setMessage("متأكد انك عايز تخرج ؟")
            AlertDialogBuilder.setCancelable(false)
            AlertDialogBuilder.setPositiveButton("نعم") { _, _ ->
                finish()
            }
            AlertDialogBuilder.setNegativeButton("لا"){_,_ ->
                Toast.makeText(this, "تم الخروج", Toast.LENGTH_LONG).show()
            }
            AlertDialogBuilder.setNeutralButton("قيمنا :D"){_,_ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.aghourservices")))
                } catch (e: ActivityNotFoundException){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.aghourservices")))
                }
            }
            val AlertDialog = AlertDialogBuilder.create()
            AlertDialog.show()
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
            }

            override fun onFailure(call: Call<ArrayList<SearchResult>?>, t: Throwable) {

            }
        })
    }

    private fun setAdapter(searchResults: ArrayList<SearchResult>) {
        if (searchResults.isEmpty()) {
            noDataTv.visibility = View.VISIBLE
            searchResultRecycler.visibility = View.GONE
            return
        }
        noDataTv.visibility = View.GONE
        searchResultRecycler.visibility = View.VISIBLE
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
        backButton = findViewById(R.id.back_btn)
        searchToolbar = findViewById(R.id.searchToolbar)
        searchEditText = findViewById(R.id.search_text)
        noDataTv = findViewById(R.id.no_data_text)
        searchResultRecycler = findViewById(R.id.searchResultRecycler)
    }
}