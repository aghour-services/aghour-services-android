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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.BaseActivity
import com.aghourservices.R
import com.aghourservices.ads.AghourAdManager
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.Category
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.databinding.ActivityCategoriesBinding
import com.aghourservices.firms.FirmsActivity
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
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var realm: Realm
    private lateinit var adView: AdView
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityCategoriesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AghourServices)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        swipeCategory()

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("category.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .allowWritesOnUiThread(true)
            .build()
        realm = Realm.getInstance(config)
        adView = findViewById(R.id.adView)
        AghourAdManager.displayBannerAd(this, adView)

        //recyclerView initialize
        binding.categoriesRecyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        binding.categoriesRecyclerview.layoutManager = linearLayoutManager
        binding.categoriesRecyclerview.layoutManager = GridLayoutManager(this, 2)

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
                binding.categoriesRecyclerview.adapter = adapter
                progressBar()
            }

            override fun onFailure(call: Call<ArrayList<Category>?>, t: Throwable) {
                val result = realm.where(Category::class.java).findAll()
                categoryList = ArrayList()
                categoryList.addAll(result)
                adapter = CategoriesAdapter(categoryList) { position -> onListItemClick(position) }
                binding.categoriesRecyclerview.adapter = adapter

                //shimmer Animation without Internet
                Toast.makeText(this@CategoriesActivity, "لا يوجد انترنت", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@CategoriesActivity, "لا يوجد انترنت", Toast.LENGTH_SHORT).show()
                progressBar()
            }
        })
    }

    private fun progressBar() {
        binding.progressBar.visibility = View.GONE
        binding.categoriesRecyclerview.visibility = View.VISIBLE
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

    //refresh
    private fun swipeCategory() {
        runnable = Runnable { loadCategoriesList() }
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 0)
//        binding.swipe.setColorSchemeResources(R.color.swipeColor)
//        binding.swipe.setOnRefreshListener {
//            Handler(Looper.getMainLooper()).postDelayed({
//                binding.swipe.isRefreshing = false
//                loadCategoriesList()
//            }, 500)
//        }
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
            R.id.nav_fav -> Toast.makeText(this, "المفضلة", Toast.LENGTH_SHORT).show()
            R.id.nav_share -> {
                shareApp()
            }
            R.id.nav_rate -> {
                rateApp()
            }
            R.id.nav_faceBook -> {
                facebook()
            }
            R.id.nav_log -> {
                logOut()
            }
        }
        binding.drawerLayout.closeDrawer(Gravity.START)
        return true
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
            binding.drawerLayout.closeDrawer(Gravity.START)
        } else {
            finishAffinity()
        }
    }
}