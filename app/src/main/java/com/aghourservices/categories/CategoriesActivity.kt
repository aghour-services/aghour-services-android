package com.aghourservices.categories

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.Category
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.firms.FirmsActivity
import com.aghourservices.firms.api.Firm
import com.aghourservices.firms.ui.FirmsAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdView
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

const val BASE_URL = "https://aghour-services.magdi.work/api/"


class CategoriesActivity : AppCompatActivity() {

    //Global initialize
    lateinit var adapter: CategoriesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var toolBar: Toolbar
    private lateinit var recyclerview: RecyclerView
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var adView: AdView
    private lateinit var realm: Realm
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var editText: AppCompatEditText
    private lateinit var noSearchResultsFoundText: TextView
    private lateinit var clearQueryImageView: ImageView
    private lateinit var voiceSearchImageView: ImageView





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        initViews()

        runnable = Runnable { loadCategoriesList() }
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1000)
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("category.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .allowWritesOnUiThread(true)
            .build()
        realm = Realm.getInstance(config)
        setSupportActionBar(toolBar)
        AghourAdManager.displayBannerAd(this, adView)

        //recyclerView initialize
        recyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = linearLayoutManager
        recyclerview.layoutManager = GridLayoutManager(this, 2)

        //Call SwipeRefreshLayout
        var number = 0
        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            number++
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
                loadCategoriesList()
            }, 1200)
        }
        editText = findViewById(R.id.search_edit_text)

        editText.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().toLowerCase(Locale.getDefault())
            filterWithQuery(query)
            toggleSearchIcons(query)
        }



    }
    private fun filterWithQuery(query: String) {
        if (query.isNotEmpty()) {
            val filteredList: ArrayList<Category> = onQueryChanged(query)
            adapter = CategoriesAdapter(filteredList) { position -> onListItemClick(position) }
            recyclerview.adapter = adapter
            toggleRecyclerView(filteredList)
        } else if (query.isEmpty()) {
            adapter = CategoriesAdapter(categoryList) { position -> onListItemClick(position) }
            recyclerview.adapter = adapter
        }
    }
    private fun onQueryChanged(filterQuery: String): ArrayList<Category> {
        val filteredList = ArrayList<Category>()
        for (CurrenetItem in categoryList) {
            if (CurrenetItem.name!!.toLowerCase(Locale.getDefault()).contains(filterQuery)) {
                filteredList.add(CurrenetItem)
            }
        }
        return filteredList
    }
    private fun toggleRecyclerView(sportsList: List<Category>) {
        if (sportsList.isEmpty()) {
            recyclerview.visibility = View.INVISIBLE
//            noSearchResultsFoundText.visibility = View.VISIBLE
        } else {
            recyclerview.visibility = View.VISIBLE
//            noSearchResultsFoundText.visibility = View.INVISIBLE
        }
    }

    private fun toggleSearchIcons(query: String) {
        if (query.isNotEmpty()) {
            clearQueryImageView.visibility = View.VISIBLE
            voiceSearchImageView.visibility = View.INVISIBLE
        } else if (query.isEmpty()) {
            clearQueryImageView.visibility = View.INVISIBLE
            voiceSearchImageView.visibility = View.VISIBLE
        }
    }



    //LoadCategoriesList With RetrofitBuilder
    private fun loadCategoriesList() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)
        val retrofitData = retrofitBuilder.loadCategoriesList()

        retrofitData.enqueue(object : Callback<ArrayList<Category>?> {

            override fun onResponse(
                call: Call<ArrayList<Category>?>,
                response: Response<ArrayList<Category>?>,
            ) {
                val responseBody = response.body()!!
                categoryList = responseBody
                realm.executeTransaction {
                    for (i in categoryList) {
                        try {
                            val category = realm.createObject(Category::class.java, i.id)
                            category.name = i.name
                            category.icon = i.icon
                        } catch (e: Exception) {
                        }
                    }

                }
                adapter = CategoriesAdapter(responseBody) { position -> onListItemClick(position) }
                recyclerview.adapter = adapter
                stopShimmerAnimation()
            }

            override fun onFailure(call: Call<ArrayList<Category>?>, t: Throwable) {
                val result = realm.where(Category::class.java).findAll()
                categoryList = ArrayList()
                categoryList.addAll(result)
                recyclerview.adapter =
                    CategoriesAdapter(categoryList) { position -> onListItemClick(position) }

                //shimmer Animation without Internet
                Toast.makeText(this@CategoriesActivity, "لا يوجد انترنت", Toast.LENGTH_SHORT).show()
                stopShimmerAnimation()
            }
        })
    }

    //load Shimmer Animation
    private fun stopShimmerAnimation() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE
    }


    //Start FirmsActivity With putExtra Data
    private fun onListItemClick(position: Int) {
        val categoryId = categoryList[position].id
        val categoryName = categoryList[position].name

        val intent = Intent(this, FirmsActivity::class.java)
        intent.putExtra("category_id", categoryId)
        intent.putExtra("category_name", categoryName)
        startActivity(intent)
    }

    //Id Fun
    private fun initViews() {
        toolBar = findViewById(R.id.toolBar)
        recyclerview = findViewById(R.id.recyclerview)
        adView = findViewById(R.id.adView)
        shimmerLayout = findViewById(R.id.shimmerLayout)
        clearQueryImageView = findViewById(R.id.clear_search_query)
        voiceSearchImageView = findViewById(R.id.voice_search_query)
    }

    //Share Button View and Create
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Share Button Action
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareButton -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.aghourservices"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
//    private fun filter(text: String) {
//        //new array list that will hold the filtered data
//        val filteredNames = ArrayList<Category>()
//        //looping through existing elements and adding the element to filtered list
//        categoryList.filterTo(filteredNames) {
//            //if the existing elements contains the search input
//            it.name!!.toLowerCase().contains(text.toLowerCase())
//        }
//        //calling a method of the adapter class and passing the filtered list
//        adapter!!.filterList(filteredNames)
//    }
}
//val editTextSearch:EditText
//editTextSearch=findViewById(R.id.editTextSearch)
//editTextSearch.addTextChangedListener(object :TextWatcher{
//    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//    }
//
//    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//    }
//
//    override fun afterTextChanged(editable: Editable?) {
//        filter(editable.toString())
//    }
//
//})