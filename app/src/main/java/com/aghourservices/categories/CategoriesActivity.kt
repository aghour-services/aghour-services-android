package com.aghourservices.categories

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aghourservices.BaseActivity
import com.aghourservices.R
import com.aghourservices.search.SearchActivity
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.Category
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.firms.FirmsActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdView
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://aghour-services.magdi.work/api/"


class CategoriesActivity : BaseActivity() {

    //Global initialize
    lateinit var adapter: CategoriesAdapter
    private lateinit var toolBar: Toolbar
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerview: RecyclerView
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var adView: AdView
    private lateinit var realm: Realm
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        initViews()

        setSupportActionBar(toolBar)
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
        AghourAdManager.displayBannerAd(this, adView)

        //recyclerView initialize
        recyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = linearLayoutManager
        recyclerview.layoutManager = GridLayoutManager(this, 2)

        //Call SwipeRefreshLayout
        var number = 0
        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setColorSchemeResources(R.color.blue200)
        swipeRefreshLayout.setOnRefreshListener {
            number++
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
                loadCategoriesList()
            }, 1500)
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
        toolBar = findViewById(R.id.toolbar)
        recyclerview = findViewById(R.id.recyclerview)
        adView = findViewById(R.id.adView)
        shimmerLayout = findViewById(R.id.shimmerLayout)
    }
}