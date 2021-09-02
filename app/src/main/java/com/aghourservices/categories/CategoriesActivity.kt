package com.aghourservices.categories

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.R
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.CategoryItem
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.firms.MarketsActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.FieldPosition

const val BASE_URL = "https://aghour-services-backend.herokuapp.com/api/"

class CategoriesActivity : AppCompatActivity() {
    lateinit var adapter: CategoriesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var toolBar: Toolbar;
    private lateinit var recyclerview: RecyclerView;
    private lateinit var categoryList: ArrayList<CategoryItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        initViews()
        setSupportActionBar(toolBar)

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
                response: Response<ArrayList<CategoryItem>?>,
            ) {
                val responseBody = response.body()!!
                categoryList = responseBody
                adapter = CategoriesAdapter(responseBody) { position -> onListItemClick(position) }
                recyclerview.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<CategoryItem>?>, t: Throwable) {
                Log.d("MainActivity", "onFailure: " + t.message)
            }
        })
    }

    //Start Second Intent Activity
    private fun onListItemClick(position: Int) {
        var categoryId = categoryList[position].id
        var categoryName = categoryList[position].name

        val intent = Intent(this, MarketsActivity::class.java)
        intent.putExtra("category_id", categoryId)
        intent.putExtra("category_name", categoryName)

        startActivity(intent)
    }

    private fun initViews() {
        toolBar = findViewById(R.id.toolBar)
        recyclerview = findViewById(R.id.recyclerview)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareButton -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.aghourservices")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
//            this.startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }
}