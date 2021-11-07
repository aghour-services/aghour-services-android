package com.aghourservices.categories

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aghourservices.BaseActivity
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.Category
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.firms.FirmsActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdView
import com.google.android.material.navigation.NavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://aghour-services.magdi.work/api/"


class CategoriesActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

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
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        initViews()

        setSupportActionBar(toolBar)
        runnable = Runnable { loadCategoriesList() }
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1000)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener(this)

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
        swipeRefreshLayout.setColorSchemeResources(R.color.swipeColor)
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
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when {
            toggle.onOptionsItemSelected(item) -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("WrongConstant")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> Toast.makeText(this, "جاري البحث", Toast.LENGTH_SHORT).show()
            R.id.nav_fav -> Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            R.id.nav_share -> {
                shareApp()
            }
            R.id.nav_log -> Toast.makeText(this, "Clicked Log out", Toast.LENGTH_SHORT).show()
            R.id.nav_rate -> {
                rateApp()
            }
            R.id.nav_faceBook -> {
                facebook()
            }
        }
        drawerLayout.closeDrawer(Gravity.START)
        return true
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        } else {
            super.onBackPressed()
        }
    }
}