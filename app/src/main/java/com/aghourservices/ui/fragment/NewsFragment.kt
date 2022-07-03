package com.aghourservices.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentNewsBinding
import com.aghourservices.ui.adapter.ArticlesAdapter
import io.realm.Realm
import io.realm.RealmConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : BaseFragment() {
    private lateinit var adapter: ArticlesAdapter
    private lateinit var articleList: ArrayList<Article>
    private lateinit var realm: Realm
    private lateinit var handler: Handler
    private lateinit var binding: FragmentNewsBinding
    private var categoryId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.news_fragment)

        try {
            init()
            loadArticles(categoryId)
            refresh()
        } catch (e: Exception) {
        }
    }

    private fun refresh() {
        try {
            handler = Handler(Looper.getMainLooper()!!)
            binding.swipe.setColorSchemeResources(R.color.swipeColor)
            binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
            binding.swipe.setOnRefreshListener {
                handler.postDelayed({
                    binding.swipe.isRefreshing = false
                    loadArticles(categoryId)
                }, 1000)
            }
        } catch (e: Exception) {
            Log.e("Exception: ", e.message!!)
        }

        binding.addArticle.setOnClickListener {
            val action = NewsFragmentDirections.actionNewsFragmentToAddArticleFragment()
            findNavController().navigate(action)
        }
    }

    private fun loadArticles(categoryId: Int) {
        val retrofitBuilder = activity?.let {
            RetrofitInstance(it).newsApi.loadArticles(categoryId)
        }

        retrofitBuilder?.enqueue(object : Callback<ArrayList<Article>?> {
            override fun onResponse(
                call: Call<ArrayList<Article>?>,
                response: Response<ArrayList<Article>?>,
            ) {
                articleList = response.body()!!
                realm.executeTransaction {
                    for (i in articleList) {
                        try {
                            val article = realm.createObject(
                                Article::class.java,
                                i.id
                            )
                            article.description = i.description
                            article.created_at = i.created_at
                        } catch (e: Exception) {
                        }
                    }
                }
                setAdapter(articleList)
                stopShimmerAnimation()
            }

            override fun onFailure(
                call: Call<ArrayList<Article>?>,
                t: Throwable
            ) {
                val result =
                    realm.where(Article::class.java).findAll()
                articleList = ArrayList()
                articleList.addAll(result)
                setAdapter(articleList)
                stopShimmerAnimation()
                if (articleList.isEmpty()) {
                    noInternetConnection()
                }
            }
        })
    }

    private fun setAdapter(articleList: ArrayList<Article>) {
        try {
            adapter = ArticlesAdapter(requireContext(), articleList) { position ->
                onListItemClick(position)
            }
            binding.newsRecyclerview.adapter = adapter
        } catch (e: Exception) {
        }
    }

    private fun onListItemClick(position: Int) {
        articleList[position]
    }

    private fun init() {
        Realm.init(requireActivity())
        val config = RealmConfiguration
            .Builder()
            .allowWritesOnUiThread(true)
            .name("article.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.getInstance(config)
        binding.newsRecyclerview.setHasFixedSize(true)
        binding.newsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
    }

    fun noInternetConnection() {
        binding.noInternet.visibility = View.VISIBLE
        binding.newsRecyclerview.visibility = View.GONE
    }

    private fun stopShimmerAnimation() {
        binding.newsShimmer.stopShimmer()
        binding.newsShimmer.visibility = View.GONE
        binding.newsRecyclerview.visibility = View.VISIBLE
    }
}