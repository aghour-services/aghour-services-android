package com.aghourservices.categories

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.listView.ListActivity
import com.aghourservices.R
import com.aghourservices.api.ApiServices
import com.aghourservices.api.CategoryItem
import com.aghourservices.categories.ui.CategoriesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.my_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class CategoriesActivity : AppCompatActivity() {

    lateinit var adapter: CategoriesAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    val data = ArrayList<CategoryItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(myToolbar)

        recyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = linearLayoutManager
        loadCategoriesList()
        recyclerview.layoutManager = GridLayoutManager(this, 2)
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
                adapter = CategoriesAdapter(responseBody) { position -> onListItemClick(position) }
                recyclerview.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<CategoryItem>?>, t: Throwable) {
                Log.d("MainActivity", "onFailure: " + t.message)
            }
        })
    }

    private fun onListItemClick(position: Int) {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }
}