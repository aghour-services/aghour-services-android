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
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.FragmentNewsBinding
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.factory.ArticlesViewModelFactory
import com.aghourservices.ui.viewModel.NewsViewModel
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import io.realm.Realm

class NewsFragment : BaseFragment(), ShowSoftKeyboard {
    private lateinit var binding: FragmentNewsBinding
    private val newsViewModel: NewsViewModel by viewModels { ArticlesViewModelFactory() }
    private val newsAdapter = ArticlesAdapter { view, position -> onListItemClick(view, position) }
    private val realm: Realm = Realm.getDefaultInstance()

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

        when (v.id) {
            R.id.news_card_view -> {
                val newsFragment = NewsFragmentDirections.actionNewsFragmentToCommentsFragment(
                    articleId
                )
                findNavController().navigate(newsFragment)
            }
            R.id.comment_tv -> {
                val newsFragment = NewsFragmentDirections.actionNewsFragmentToCommentsFragment(
                    articleId
                )
                findNavController().navigate(newsFragment)
            }
            R.id.user_layout -> {
                val newsFragment = NewsFragmentDirections.actionNewsFragmentToCommentsFragment(
                    articleId
                )
                findNavController().navigate(newsFragment)
            }

            R.id.news_favorite -> {
                updateFavorite(position)
            }
        }
    }

    private fun updateFavorite(position: Int) {
        val article = newsAdapter.getArticle(position)
        realm.executeTransaction {
            article.isFavorite = !article.isFavorite
            realm.createOrUpdateObjectFromJson(Article::class.java, article.toJSONObject())
        }
//        newsAdapter.notifyItemChanged(position)
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