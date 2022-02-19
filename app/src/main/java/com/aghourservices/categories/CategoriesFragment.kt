package com.aghourservices.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.categories.api.ApiServices
import com.aghourservices.categories.api.Category
import com.aghourservices.categories.ui.CategoriesAdapter
import com.aghourservices.databinding.FragmentCategoriesBinding
import com.aghourservices.firms.FirmsFragment
import com.aghourservices.settings.ThemePreference
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://aghour-services.magdi.work/api/"

class CategoriesFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var realm: Realm
    private lateinit var adapter: CategoriesAdapter
    private lateinit var categoryList: ArrayList<Category>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNav()
        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.show()
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
        requireActivity().title = getString(R.string.categories_fragment)

        binding.categoriesRecyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.categoriesRecyclerview.layoutManager = linearLayoutManager
        binding.categoriesRecyclerview.layoutManager = GridLayoutManager(activity, 2)
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
                progressBar()
                try {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        "لا يوجد إنترنت",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                }
            }
        })
    }

    private fun onListItemClick(position: Int) {
        val categoryId = categoryList[position].id
        val categoryName = categoryList[position].name
        val fragmentManager = requireActivity().supportFragmentManager
        val arguments = Bundle()
        arguments.putInt("category_id", categoryId)
        arguments.putString("category_name", categoryName)
        val firmsFragment = FirmsFragment()
        firmsFragment.arguments = arguments
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.parent_container, firmsFragment)
            .addToBackStack("Firms").commit()
    }

    private fun progressBar() {
        binding.progressBar.visibility = View.GONE
        binding.categoriesRecyclerview.visibility = View.VISIBLE
    }
}