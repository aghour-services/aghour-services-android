package com.aghourservices.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.databinding.FragmentDraftArticlesBinding
import com.aghourservices.ui.adapter.DraftArticlesAdapter
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.viewModel.ArticleViewModel

class DraftArticlesFragment : BaseFragment() {
    private lateinit var binding: FragmentDraftArticlesBinding
    private val articleViewModel: ArticleViewModel by viewModels()
    private val draftArticlesAdapter =
        DraftArticlesAdapter { view, position -> onListItemClick(view, position) }
    private val userToken: String by lazy { UserInfo.getUserData(requireContext()).token }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDraftArticlesBinding.inflate(layoutInflater)
        requireActivity().title = getString(R.string.draft_articles)
        initDraftArticlesObserve()
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
    }

    private fun initRecyclerView() {
        binding.newsRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = draftArticlesAdapter
        }
    }

    private fun initDraftArticlesObserve() {
        articleViewModel.newsLiveData.observe(viewLifecycleOwner) { articles ->
            draftArticlesAdapter.setArticles(articles)
            if (articles.isEmpty()) {
                Toast.makeText(requireContext(), "لا توجد أخبار مؤجلة", Toast.LENGTH_SHORT).show()
            }
        }
        articleViewModel.draftArticles(
            requireContext(),
            userToken,
            UserInfo.getFCMToken(requireContext())
        )
    }

    private fun onListItemClick(v: View, position: Int) {
        val article = draftArticlesAdapter.getArticle(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigation()
    }
}