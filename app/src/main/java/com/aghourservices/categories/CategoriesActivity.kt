package com.aghourservices.categories

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.CategoryItem
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.markets.MarketsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://aghour-services-backend.herokuapp.com/api/"
class CategoriesActivity : AppCompatActivity() {
    lateinit var adapter: CategoriesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(Toolbar)

        recyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = linearLayoutManager
        loadCategoriesList()
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        val animation : Animation = AnimationUtils.loadAnimation(this, R.anim.down_side)
        recyclerview.startAnimation(animation)

    }

    private fun loadCategoriesList() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).build().create(ApiServices::class.java)

        val retrofitData = retrofitBuilder.loadCategoriesList()

        retrofitData.enqueue(object : Callback<ArrayList<CategoryItem>?> {

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<CategoryItem>?>,
                response: Response<ArrayList<CategoryItem>?>
            ) {
                val responseBody = response.body()!!
                Log.v("DATA", responseBody.toString())
                adapter = CategoriesAdapter(responseBody) { onListItemClick() }
                recyclerview.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<CategoryItem>?>, t: Throwable) {
                Log.d("MainActivity", "onFailure: " + t.message)
            }
        })
    }

    //Start Second Intent Activity
    private fun onListItemClick() {
        val intent = Intent(this, MarketsActivity::class.java)
        startActivity(intent)
    }
}