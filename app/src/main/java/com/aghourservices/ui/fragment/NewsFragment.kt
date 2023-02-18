package com.aghourservices.ui.fragment

import android.os.Bundle
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
import com.aghourservices.databinding.FragmentNewsBinding
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.viewModel.NewsViewModel
import com.aghourservices.utils.helper.Intents
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : BaseFragment() {
    private lateinit var binding: FragmentNewsBinding
    private val newsViewModel: NewsViewModel by viewModels()
    private val newsAdapter = ArticlesAdapter { view, position -> onListItemClick(view, position) }
    private val deviceId: String by lazy { Intents.getDeviceId(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(layoutInflater)
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
        newsViewModel.loadArticles(requireContext(), deviceId)
    }

    private fun refresh() {
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            newsViewModel.loadArticles(requireContext(), deviceId)
        }
    }

    private fun onListItemClick(v: View, position: Int) {
        val articleId = newsAdapter.getArticle(position).id
        val userName = newsAdapter.getArticle(position).user?.name!!
        val time = newsAdapter.getArticle(position).created_at
        val description = newsAdapter.getArticle(position).description
        val commentsFragment = NewsFragmentDirections.actionNewsFragmentToCommentsFragment(
            articleId,
            userName,
            time,
            description
        )

        val editArticleFragment = NewsFragmentDirections.actionNewsFragmentToEditArticleFragment(
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
        val userDetails = UserInfo.getUserData(requireContext())
        val articleId = newsAdapter.getArticle(position).id

        val retrofitBuilder = RetrofitInstance(requireContext()).newsApi.deleteArticle(
            articleId,
            userDetails.token,
            deviceId
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