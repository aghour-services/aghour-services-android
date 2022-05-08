package com.aghourservices.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.cache.UserInfo
import com.aghourservices.constants.RetrofitInstance
import com.aghourservices.databinding.FragmentNewsBinding
import com.aghourservices.interfaces.AlertDialog
import com.aghourservices.news.api.Article
import com.aghourservices.news.api.ArticlesAPI
import com.aghourservices.news.api.CreateArticle
import com.aghourservices.news.policies.UserAbility
import com.aghourservices.news.ui.ArticlesAdapter
import com.aghourservices.user.User
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
        showBottomNav()

        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.show()

        try {
            init()
            loadArticles(categoryId)
            refresh()
        } catch (e: Exception) {
        }
    }

    fun toViewUI(user: User) {
        if(UserAbility(user).canPublish()) {
            // view ui
        }
    }

    private fun createArticle(article: Article) {
        val user = UserInfo().getUserData(requireActivity())
        val retrofitBuilder = RetrofitInstance(requireActivity()).retrofit.create(CreateFirm::class.java)
        val retrofitData = retrofitBuilder.createFirm(article.toJsonObject(), user.token)
        retrofitData.enqueue(object : Callback<Firm> {
            override fun onResponse(call: Call<Firm>, response: Response<Firm>) {
                AlertDialog.dataAdded(requireContext())
                setTextEmpty()
            }

            override fun onFailure(call: Call<Firm>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
    }

    private fun setTextEmpty() {
        Toast.makeText(context, "to be implemented", Toast.LENGTH_SHORT).show()
//        binding.name.text.clear()
//        binding.address.text.clear()
//        binding.description.text.clear()
//        binding.phoneNumber.text.clear()
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

    fun toViewUI(user: User) {
        if (UserAbility(user).canPublish()) {
            // view ui
        }
    }

    private fun createArticle(article: Article) {
        val user = UserInfo().getUserData(requireActivity())
        val retrofitBuilder = RetrofitInstance.retrofit.create(CreateArticle::class.java)
        val retrofitData = retrofitBuilder.createArticle(article.toJsonObject(), user.token)
        retrofitData.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                AlertDialog.dataAdded(requireContext())
                setTextEmpty()
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
    }

    private fun setTextEmpty() {
//        binding.name.text.clear()
//        binding.address.text.clear()
//        binding.description.text.clear()
//        binding.phoneNumber.text.clear()
    }

    private fun loadArticles(categoryId: Int) {
        val retrofitBuilder = RetrofitInstance(requireActivity()).retrofit.create(ArticlesAPI::class.java)
        val retrofitData = retrofitBuilder.loadArticles(categoryId)
        retrofitData.enqueue(object : Callback<ArrayList<Article>?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ArrayList<Article>?>,
                response: Response<ArrayList<Article>?>,
            ) {
                articleList = response.body()!!
                realm.executeTransaction {
                    for (i in articleList) {
                        try {
                            val article = realm.createObject(Article::class.java, i.id)
                            article.description = i.description
                            article.created_at = i.created_at
                        } catch (e: Exception) {
                        }
                    }
                }
                setAdapter(articleList)
                stopShimmerAnimation()
            }

            override fun onFailure(call: Call<ArrayList<Article>?>, t: Throwable) {
                val result = realm.where(Article::class.java).findAll()
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
        val article = articleList[position]
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