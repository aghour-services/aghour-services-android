package com.aghourservices.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Profile
import com.aghourservices.data.network.RetrofitInstance
import com.aghourservices.databinding.FragmentPublishedArticlesBinding
import com.aghourservices.ui.adapters.PublishedArticlesAdapter
import com.aghourservices.ui.viewModels.PublishedArticleViewModel
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PublishedArticleFragment : BaseFragment() {
    private lateinit var binding: FragmentPublishedArticlesBinding
    private val publishedArticleViewModel: PublishedArticleViewModel by viewModels()
    private val publishedArticlesAdapter =
        PublishedArticlesAdapter { view, position -> onListItemClick(view, position) }
    private val userToken: String by lazy { getUserData(requireContext()).token }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPublishedArticlesBinding.inflate(layoutInflater)
        requireActivity().title = getString(R.string.news_fragment)
        initRecyclerView()
        initNewsObserve()
        getProfile()
        refresh()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavigation()

        binding.draftArticlesBtn.setOnClickListener {
            findNavController().navigate(PublishedArticleFragmentDirections.actionNewsFragmentToDraftArticlesFragment())
        }
    }

    private fun initRecyclerView() {
        binding.newsRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = publishedArticlesAdapter
        }
    }

    private fun initNewsObserve() {
        publishedArticleViewModel.newsLiveData.observe(viewLifecycleOwner) { articles ->
            publishedArticlesAdapter.setArticles(articles)
            stopShimmerAnimation()
            if (articles.isEmpty()) {
                noInternetConnection()
            }
        }
        publishedArticleViewModel.loadArticles(requireContext(), userToken, getFCMToken(requireContext()))
    }

    private fun refresh() {
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            publishedArticleViewModel.loadArticles(
                requireContext(),
                userToken,
                getFCMToken(requireContext())
            )
        }
    }

    private fun onListItemClick(v: View, position: Int) {
        val article = publishedArticlesAdapter.getArticle(position)
        val userName = publishedArticlesAdapter.getArticle(position).user?.name!!
        val description = publishedArticlesAdapter.getArticle(position).description
        val commentsFragment =
            PublishedArticleFragmentDirections.actionNewsFragmentToCommentsFragment(article.id)
        val commentsDialogSheet = PublishedArticleFragmentDirections.actionNewsFragmentToCommentsDialogSheet(
            article.id,
            article.likes_count
        )
        val editArticleFragment = PublishedArticleFragmentDirections.actionNewsFragmentToEditArticleFragment(
            article.id,
            description,
            userName
        )
        val likesDialogSheet = PublishedArticleFragmentDirections.actionNewsFragmentToLikesDialogSheet(
            article.id,
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
                        R.id.edit -> {
                            findNavController().navigate(editArticleFragment)
                        }

                        R.id.delete -> {
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
                findNavController().navigate(likesDialogSheet)
            }
            R.id.comments_count -> {
                findNavController().navigate(commentsDialogSheet)
            }
        }
    }

    private fun deleteArticleDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("حذف الخبر")
        alertDialogBuilder.setMessage("أنت على وشك حذف الخبر")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            publishedArticleViewModel.deleteArticle(
                requireContext(),
                userToken,
                publishedArticlesAdapter,
                position
            )
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negativeButton)) { _, _ -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun updateLikeArticle(position: Int) {
        val article = publishedArticlesAdapter.getArticle(position)
        if (userToken.isEmpty()) {
            com.aghourservices.utils.helper.AlertDialogs.createAccount(
                requireContext(),
                "للإعجاب بالخبر يجب إنشاء حساب أولا"
            )
        } else {
            if (article.liked) {
                publishedArticleViewModel.unLikeArticle(
                    userToken,
                    publishedArticlesAdapter,
                    position
                )
            } else {
                publishedArticleViewModel.likeArticle(
                    userToken,
                    publishedArticlesAdapter,
                    position
                )
            }
        }
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

    private fun getProfile() {
        val retrofitInstance = RetrofitInstance.userApi.userProfile(userToken)

        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                binding.draftArticlesBtn.isVisible = response.body()?.verified == true
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {}
        })
    }
}