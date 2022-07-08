package com.aghourservices.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.FragmentNewsBinding
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.viewModel.NewsViewModel

class NewsFragment : BaseFragment() {
    private lateinit var newsAdapter: ArticlesAdapter
    private lateinit var articleList: ArrayList<Article>
    private lateinit var handler: Handler
    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsViewModel: NewsViewModel
    private var categoryId = 0

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
            newsRecyclerview.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setUpViewModel() {
        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        activity?.let { newsViewModel.loadArticles(it, categoryId) }
        newsViewModel.newsLiveData.observe(viewLifecycleOwner) {
            articleList = it
            newsAdapter =
                ArticlesAdapter(requireContext(), it) { onListItemClick() }
            binding.newsRecyclerview.adapter = newsAdapter
            stopShimmerAnimation()
            if (articleList.isEmpty()) {
                noInternetConnection()
            }
        }
    }

    private fun refresh() {
        handler = Handler(Looper.getMainLooper()!!)
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            handler.postDelayed({
                binding.swipe.isRefreshing = false
                activity?.let { newsViewModel.loadArticles(it, categoryId) }
            }, 1000)
        }

        binding.addArticle.setOnClickListener {
            val action = NewsFragmentDirections.actionNewsFragmentToAddArticleFragment()
            findNavController().navigate(action)
        }
    }

    private fun onListItemClick() {}

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