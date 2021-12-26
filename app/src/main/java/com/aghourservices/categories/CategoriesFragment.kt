package com.aghourservices.categories

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.ads.Banner
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.Category
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.databinding.FragmentCategoriesBinding
import com.google.android.gms.ads.AdView
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class CategoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var realm: Realm
    private lateinit var adapter: CategoriesAdapter
    private lateinit var categoryList: ArrayList<Category>

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var adView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        loadCategoriesList()
    }

    private fun init() {
        Realm.init(requireContext())
        val config = RealmConfiguration.Builder()
            .name("category.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .allowWritesOnUiThread(true)
            .build()
        realm = Realm.getInstance(config)

        //recyclerView initialize
        binding.categoriesRecyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.categoriesRecyclerview.layoutManager = linearLayoutManager
        binding.categoriesRecyclerview.layoutManager = GridLayoutManager(activity, 2)

        adView = requireActivity().findViewById(R.id.adView)
        Banner.show(requireActivity(), adView)
    }

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
                Toast.makeText(activity, "لا يوجد انترنت", Toast.LENGTH_SHORT).show()
                progressBar()
            }
        })
    }


    //Start FirmsActivity With putExtra Data
    private fun onListItemClick(position: Int) {
        val categoryId = categoryList[position].id
        val categoryName = categoryList[position].name
        Toast.makeText(activity, categoryName, Toast.LENGTH_SHORT).show()

//        val intent = Intent(this, FirmsActivity::class.java)
//        intent.putExtra("category_id", categoryId)
//        intent.putExtra("category_name", categoryName)
//        startActivity(intent)
    }

    //refresh
//    private fun swipeCategory() {
//        runnable = Runnable { loadCategoriesList() }
//        handler = Handler(Looper.getMainLooper())
//        handler.postDelayed(runnable, 0)
//    }

    private fun progressBar() {
        binding.progressBar.visibility = View.GONE
        binding.categoriesRecyclerview.visibility = View.VISIBLE
    }

}