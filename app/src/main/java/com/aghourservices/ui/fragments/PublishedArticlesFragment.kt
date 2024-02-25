package com.aghourservices.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.aghourservices.ui.base.BaseFragment
import com.aghourservices.ui.viewModels.PublishedArticlesViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PublishedArticlesFragment : BaseFragment() {
    private lateinit var binding: FragmentPublishedArticlesBinding
    private val publishedArticlesViewModel: PublishedArticlesViewModel by viewModels()
    private val publishedArticlesAdapter =
        PublishedArticlesAdapter { view, position -> onListItemClick(view, position) }

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
        noInternetConnectionBehavior()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavigation()
        showToolbar()

        binding.draftArticlesBtn.setOnClickListener {
            findNavController().navigate(PublishedArticlesFragmentDirections.actionNewsFragmentToDraftArticlesFragment())
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
        publishedArticlesViewModel.newsLiveData.observe(viewLifecycleOwner) { articles ->
            publishedArticlesAdapter.setArticles(articles)
            stopShimmerAnimation()
            if (articles.isEmpty()) {
                noInternetConnection()
            }
        }
        currentUser.let { publishedArticlesViewModel.loadArticles(binding, it.token, fcmToken) }
    }

    private fun refresh() {
        binding.swipe.setColorSchemeResources(R.color.swipeColor)
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            publishedArticlesViewModel.loadArticles(
                binding,
                currentUser.token,
                fcmToken
            )
        }
    }

    private fun onListItemClick(v: View, position: Int) {
        val article = publishedArticlesAdapter.getArticle(position)
        val user = publishedArticlesAdapter.getArticle(position).user!!
        val description = publishedArticlesAdapter.getArticle(position).description

        val commentsFragment =
            PublishedArticlesFragmentDirections.actionNewsFragmentToShowOneArticleFragment(article.id)

        val commentsDialogSheet =
            PublishedArticlesFragmentDirections.actionNewsFragmentToCommentsDialogSheet(
                article.id,
                article.likes_count
            )

        val editArticleFragment =
            PublishedArticlesFragmentDirections.actionNewsFragmentToEditArticleFragment(
                article.id,
                description,
                user.name
            )

        val likesDialogSheet =
            PublishedArticlesFragmentDirections.actionNewsFragmentToLikesDialogSheet(
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

            R.id.avatar_image -> {
                fullScreenAvatar(user.url, user.name, null)

                Log.d("USER_AVATAR", "onListItemClick: ${user.url}")
            }

            R.id.article_image -> {
                fullScreenArticleAttachments(article.attachments?.last()?.resource_url, null)
            }
        }
    }

    private fun deleteArticleDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("حذف الخبر")
        alertDialogBuilder.setMessage("أنت على وشك حذف الخبر")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            publishedArticlesViewModel.deleteArticle(
                requireContext(),
                currentUser.token,
                publishedArticlesAdapter,
                position,
                fcmToken
            )
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negativeButton)) { _, _ -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun updateLikeArticle(position: Int) {
        val article = publishedArticlesAdapter.getArticle(position)
        if (currentUser.token.isEmpty()) {
            com.aghourservices.utils.helper.AlertDialogs.createAccount(
                requireContext(),
                "للإعجاب بالخبر يجب إنشاء حساب أولا"
            )
        } else {
            if (article.liked) {
                publishedArticlesViewModel.unLikeArticle(
                    currentUser.token,
                    publishedArticlesAdapter,
                    position
                )
            } else {
                publishedArticlesViewModel.likeArticle(
                    currentUser.token,
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

    private fun noInternetConnectionBehavior() {
        binding.apply {
            tryAgainBtn.setOnClickListener {
                newsShimmer.startShimmer()
                newsShimmer.isVisible = true
                noInternet.isVisible = false
                publishedArticlesViewModel.loadArticles(
                    binding,
                    currentUser.token,
                    fcmToken
                )
            }
        }
    }

    private fun getProfile() {
        val retrofitInstance = RetrofitInstance.userApi.userProfile(currentUser.token)

        retrofitInstance.enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    binding.draftArticlesBtn.isVisible = response.body()?.verified == true
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {}
        })
    }
}