package com.aghourservices.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentArticlesBinding
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.main.cache.UserInfo.getFCMToken
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.viewModel.NewsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleFragment : BaseFragment() {
    private lateinit var binding: FragmentArticlesBinding
    private val newsViewModel: NewsViewModel by viewModels()
    private val newsAdapter = ArticlesAdapter { view, position -> onListItemClick(view, position) }
    private val userToken: String by lazy { getUserData(requireContext()).token }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticlesBinding.inflate(layoutInflater)
        requireActivity().title = getString(R.string.news_fragment)
        initRecyclerView()
        initNewsObserve()
        refresh()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavigation()
    }

    private fun initRecyclerView() {
        binding.newsRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
        }
    }

    private fun initNewsObserve() {
        newsViewModel.newsLiveData.observe(viewLifecycleOwner) { articles ->
            newsAdapter.setArticles(articles)
            stopShimmerAnimation()
            if (articles.isEmpty()) {
                noInternetConnection()
            }
        }
        newsViewModel.loadArticles(requireContext(), userToken, getFCMToken(requireContext()))
    }

    private fun refresh() {
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            newsViewModel.loadArticles(requireContext(), userToken, getFCMToken(requireContext()))
        }
    }

    private fun onListItemClick(v: View, position: Int) {
        val articleId = newsAdapter.getArticle(position).id
        val userName = newsAdapter.getArticle(position).user?.name!!
        val time = newsAdapter.getArticle(position).created_at
        val description = newsAdapter.getArticle(position).description
        val commentsFragment = ArticleFragmentDirections.actionNewsFragmentToCommentsFragment(
            articleId,
            userName,
            time,
            description
        )

        val editArticleFragment = ArticleFragmentDirections.actionNewsFragmentToEditArticleFragment(
            articleId,
            description,
            userName
        )

        when (v.id) {
            R.id.add_comment -> {
                findNavController().navigate(commentsFragment)
            }
            R.id.user_layout -> {
                findNavController().navigate(commentsFragment)
            }
            R.id.latest_comment_card -> {
                findNavController().navigate(commentsFragment)
            }
            R.id.popup_menu -> {
                val popup = PopupMenu(requireContext(), v)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit_article -> {
                            findNavController().navigate(editArticleFragment)
                        }
                        R.id.delete_article -> {
                            deleteArticleDialog(position)
                        }
                    }
                    true
                }
                popup.show()
            }
            R.id.like_article -> {
                updateLikeArticle(position)
                initNewsObserve()
            }
            R.id.likes_count -> {
                Toast.makeText(requireContext(), "Likes Count", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteArticleDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("حذف الخبر")
        alertDialogBuilder.setMessage("أنت على وشك حذف الخبر")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            deleteComment(position)
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negativeButton)) { _, _ -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteComment(position: Int) {
        val userDetails = getUserData(requireContext())
        val articleId = newsAdapter.getArticle(position).id

        val retrofitBuilder = RetrofitInstance(requireContext()).newsApi.deleteArticle(
            articleId,
            userDetails.token,
            getFCMToken(requireContext())
        )

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    newsAdapter.deleteArticle(position)
                    Toast.makeText(requireContext(), "تم مسح الخبر", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                com.aghourservices.utils.interfaces.AlertDialog.noInternet(requireContext())
            }
        })
    }

    private fun updateLikeArticle(position: Int) {
        val article = newsAdapter.getArticle(position)

        if (article.liked) {
            unLikeArticle(position)
        } else {
            likeArticle(position)
        }
    }

    private fun likeArticle(position: Int) {
        val article = newsAdapter.getArticle(position)
        article.liked = true

        val retrofitInstance = RetrofitInstance(requireContext())
        val likeApi = retrofitInstance.likeApi
        val request = likeApi.likeArticle(article.id, userToken)

        request.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    val updatedArticle = response.body()
                    if (updatedArticle != null) {
                        newsAdapter.updateArticle(position, updatedArticle)
                    }
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                Log.e("LIKE", "onFailure: ${t.message}")
            }
        })
    }

    private fun unLikeArticle(position: Int) {
        val article = newsAdapter.getArticle(position)
        article.liked = false

        val retrofitInstance = RetrofitInstance(requireContext())
        val likeApi = retrofitInstance.likeApi
        val request = likeApi.unLikeArticle(article.id, userToken)

        request.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    val updatedArticle = response.body()
                    if (updatedArticle != null) {
                        newsAdapter.updateArticle(position, updatedArticle)
                    }
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                Log.e("LIKE", "onFailure: ${t.message}")
            }
        })
    }

    private fun noInternetConnection() {
        binding.apply {
            noInternet.isVisible = true
            newsRecyclerview.isVisible = false
        }
    }

    private fun stopShimmerAnimation() {
        binding.apply {
            newsShimmer.stopShimmer()
            newsShimmer.isVisible = false
            newsRecyclerview.isVisible = true
        }
    }
}