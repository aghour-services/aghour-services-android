package com.aghourservices.ui.fragments

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
import com.aghourservices.data.network.RetrofitInstance
import com.aghourservices.databinding.FragmentDraftArticlesBinding
import com.aghourservices.ui.adapters.DraftArticlesAdapter
import com.aghourservices.ui.viewModels.DraftArticlesViewModel
import com.aghourservices.utils.services.cache.UserInfo
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DraftArticlesFragment : BaseFragment() {
    private lateinit var binding: FragmentDraftArticlesBinding
    private val draftArticlesViewModel: DraftArticlesViewModel by viewModels()
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
        refreshDraftArticles()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        showToolbar()
    }

    private fun refreshDraftArticles() {
        binding.swipeRefresh.setColorSchemeResources(R.color.swipeColor)
        binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            initDraftArticlesObserve()
        }
    }
    private fun initRecyclerView() {
        binding.newsRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = draftArticlesAdapter
        }
    }

    private fun initDraftArticlesObserve() {
        draftArticlesViewModel.newsLiveData.observe(viewLifecycleOwner) { articles ->
            draftArticlesAdapter.setArticles(articles)
            hideProgressBar()
            if (articles.isEmpty()){
                binding.lottieAnimationView.isVisible = true
                onSNACK(binding.root, "لا توجد أخبار مؤجلة")
            }
        }
        draftArticlesViewModel.draftArticles(
            requireContext(),
            userToken,
            getFCMToken(requireContext())
        )
    }

    private fun hideProgressBar() {
        binding.apply {
            draftProgressBar.isVisible = false
            newsRecyclerview.isVisible = true
        }
    }


    private fun onListItemClick(v: View, position: Int) {
        val article = draftArticlesAdapter.getArticle(position)
        val description = draftArticlesAdapter.getArticle(position).description
        val userName = draftArticlesAdapter.getArticle(position).user?.name!!

        val editDraftArticleDialog = DraftArticlesFragmentDirections.actionDraftArticlesFragmentToEditDraftArticleFragment(
            article.id,
            description,
            userName
        )

        when (v.id) {
            R.id.publish_draft_article_btn -> {
                updateArticle(article, position)
            }
            R.id.draft_article_popup_menu -> {
                val popup = PopupMenu(requireContext(), v)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit -> {
                            findNavController().navigate(editDraftArticleDialog)
                        }

                        R.id.delete -> {
                            deleteArticleDialog(position)
                        }
                    }
                    true
                }
                popup.show()
            }
        }
    }

    private fun updateArticle(article: Article, position: Int) {
        val retrofitInstance = RetrofitInstance.articlesApi.updateArticle(
            article.id,
            userToken,
            article.toJsonObject(),
            getFCMToken(requireContext())
        )

        retrofitInstance.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "تم نشر الخبر", Toast.LENGTH_SHORT).show()
                    draftArticlesAdapter.notifyItemRemoved(position)
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteArticleDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("حذف الخبر")
        alertDialogBuilder.setMessage("أنت على وشك حذف الخبر")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            draftArticlesViewModel.deleteArticle(
                requireContext(),
                userToken,
                draftArticlesAdapter,
                position
            )
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negativeButton)) { _, _ -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}