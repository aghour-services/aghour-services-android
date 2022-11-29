package com.aghourservices.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.databinding.FragmentNewsBinding
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.factory.ArticlesViewModelFactory
import com.aghourservices.ui.viewModel.NewsViewModel
import com.aghourservices.utils.interfaces.ShowSoftKeyboard

class NewsFragment : BaseFragment(), ShowSoftKeyboard {
    private lateinit var binding: FragmentNewsBinding
    private val newsViewModel: NewsViewModel by viewModels { ArticlesViewModelFactory() }
    private val newsAdapter = ArticlesAdapter { view, position -> onListItemClick(view, position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.news_fragment)
        initRecyclerView()
        setUpViewModel()
        refresh()
        showBottomNavigation()
    }

    private fun initRecyclerView() {
        binding.apply {
            newsRecyclerview.setHasFixedSize(true)
            newsRecyclerview.adapter = newsAdapter
            newsRecyclerview.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setUpViewModel() {
        newsViewModel.newsLiveData.observe(viewLifecycleOwner) { articles ->
            newsAdapter.setArticles(articles)
            stopShimmerAnimation()
            if (articles.isEmpty()) {
                noInternetConnection()
            }
        }
        newsViewModel.loadArticles(requireContext())
    }

    private fun refresh() {
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            newsViewModel.loadArticles(requireContext())
        }
    }

    private fun onListItemClick(v: View, position: Int) {
        val articleId = newsAdapter.getArticle(position).id
        val newsFragment = NewsFragmentDirections.actionNewsFragmentToCommentsFragment(articleId)

        when (v.id) {
            R.id.add_comment -> {
                findNavController().navigate(newsFragment)
            }
            R.id.user_layout -> {
                findNavController().navigate(newsFragment)
            }
            R.id.description -> {
                findNavController().navigate(newsFragment)
            }
            R.id.latest_comment_card -> {
                findNavController().navigate(newsFragment)
            }
        }
    }

//    private fun updateFavorite(position: Int) {
//        val article = newsAdapter.getArticle(position)
//        realm.executeTransaction {
//            article.isFavorite = !article.isFavorite
//            realm.createOrUpdateObjectFromJson(Article::class.java, article.toJSONObject())
//        }
////        newsAdapter.notifyItemChanged(position)
//    }

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