package com.aghourservices.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Article
import com.aghourservices.data.model.Comment
import com.aghourservices.data.network.RetrofitInstance.articlesApi
import com.aghourservices.data.network.RetrofitInstance.likeApi
import com.aghourservices.databinding.FragmentShowOneArticleBinding
import com.aghourservices.ui.adapters.CommentsAdapter
import com.aghourservices.ui.base.BaseFragment
import com.aghourservices.ui.viewModels.CommentsViewModel
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.helper.Intents.loadProfileImage
import com.aghourservices.utils.helper.Intents.showKeyboard
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowOneArticleFragment : BaseFragment() {
    private var _binding: FragmentShowOneArticleBinding? = null
    private val binding get() = _binding!!
    private val commentsViewModel: CommentsViewModel by viewModels()
    private val arguments: ShowOneArticleFragmentArgs by navArgs()
    private val commentsAdapter =
        CommentsAdapter { view, position -> onCommentItemClick(view, position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowOneArticleBinding.inflate(inflater, container, false)
        @Suppress("DEPRECATION")
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initRecyclerView()
        initCommentEdt()
        showArticle()
        refresh()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        showToolbar()
    }

    override fun onResume() {
        super.onResume()
        loadComments()
        noInternetConnectionBehavior()
    }

    private fun refresh() {
        reloadingComments()
        binding.refreshComments.setColorSchemeResources(R.color.swipeColor)
        binding.refreshComments.setProgressBackgroundColorSchemeResource(R.color.swipeBg)
        binding.refreshComments.setOnRefreshListener {
            binding.refreshComments.isRefreshing = false
            loadComments()
        }
    }

    private fun initCommentEdt() {
        binding.commentEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val commentTxt = binding.commentEdt.text.toString().trim()

                if (TextUtils.isEmpty(commentTxt)) {
                    binding.commentBtn.isEnabled = false
                } else {
                    binding.commentBtn.isEnabled = true
                    binding.commentBtn.setOnClickListener {
                        if (currentUser.token.isEmpty()) {
                            AlertDialogs.createAccount(requireContext(), "للتعليق أنشئ حساب أولا")
                        } else {
                            val comment = Comment()
                            comment.body = commentTxt
                            postComment(comment)
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showArticle() {
        val retrofitBuilder =
            articlesApi.showArticle(arguments.articleId, currentUser.token, fcmToken)

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(
                call: Call<Article>, response: Response<Article>
            ) {
                if (response.isSuccessful) {
                    val article = response.body()
                    requireActivity().title = article?.user?.name
                    binding.apply {
                        articleUserName.apply {
                            text = article?.user?.name
                            if (article?.user?.verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tooltipText = context.getString(R.string.verified)
                            } else {
                                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                            }
                        }
                        articleCreatedAt.text = article?.created_at
                        articleDescription.text = article?.description

                        article?.attachments?.forEach { attachment ->
                            Glide.with(articleImage)
                                .load(attachment.resource_url)
                                .placeholder(R.color.image_bg)
                                .encodeQuality(100)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(articleImage)

                            articleImage.isVisible = attachment.resource_url.isNotEmpty()
                        }
                        loadProfileImage(
                            requireContext(),
                            article?.user?.url.toString(),
                            avatarImage
                        )

                        binding.apply {
                            likesCount.isVisible = article?.likes_count!! > 0
                            likesCount.text = "${article.likes_count} إعجاب"

                            if (article.comments_count < 1) {
                                commentsCount.text = "لا توجد تعليقات"
                            } else {
                                commentsCount.text = "${article.comments_count} تعليق"
                            }
                        }
                    }
                    article?.let { initUserClicks(it) }
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                binding.refreshComments.isVisible = false
                binding.noInternet.isVisible = true
            }
        })
    }

    private fun postComment(comment: Comment) {
        showCommentProgressBar()
        commentsViewModel.addComment(
            requireContext(),
            arguments.articleId,
            currentUser.token,
            commentsAdapter,
            comment,
            fcmToken
        )
        commentsViewModel.addCommentLiveData.observe(viewLifecycleOwner) {
            hideCommentProgressBar()
            binding.commentEdt.text.clear()
            loadComments()
        }
    }

    private fun loadComments() {
        commentsViewModel.loadComments(
            requireContext(),
            arguments.articleId,
            fcmToken
        )
        commentsViewModel.commentsLiveData.observe(viewLifecycleOwner) {
            commentsAdapter.setComments(it)
            stopShimmerAnimation()
            binding.noComments.isVisible = it.isEmpty()
        }
    }

    private fun initRecyclerView() {
        binding.commentsRecyclerView.apply {
            setHasFixedSize(true)
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showCommentProgressBar() {
        binding.apply {
            commentProgress.isVisible = true
            commentBtn.isVisible = false
        }
    }

    private fun hideCommentProgressBar() {
        binding.apply {
            commentBtn.isVisible = true
            commentProgress.isVisible = false
        }
    }

    private fun reloadingComments() {
        binding.apply {
            commentsShimmer.isVisible = true
            commentsShimmer.startShimmer()
        }
        loadComments()
    }

    private fun stopShimmerAnimation() {
        binding.apply {
            commentsShimmer.stopShimmer()
            commentsShimmer.isVisible = false
            commentsRecyclerView.isVisible = true
        }
    }

    private fun onCommentItemClick(v: View, position: Int) {
        val comment = commentsAdapter.getComment(position)
        val user = commentsAdapter.getComment(position).user!!

        val updateComment =
            ShowOneArticleFragmentDirections.actionShowOneArticleFragmentToUpdateCommentFragment(
                arguments.articleId,
                comment.id,
                comment.body,
                comment.user?.name.toString()
            )

        binding.apply {
            avatarImage.setOnClickListener {
                fullScreenAvatar(user.url, user.name)
            }
        }

        when (v.id) {
            R.id.popup_menu -> {
                val popup = PopupMenu(requireContext(), v)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit -> {
                            findNavController().navigate(updateComment)
                        }

                        R.id.delete -> {
                            deleteCommentAlert(position)
                        }
                    }
                    true
                }
                popup.show()
            }

            R.id.avatar_image -> {
                fullScreenAvatar(user.url, user.name)
            }
        }
    }

    private fun deleteCommentAlert(position: Int) {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_comment))
        alertDialogBuilder.setMessage(getString(R.string.are_you_sure_to_delete_comment))
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            val commentId = commentsAdapter.getComment(position).id
            commentsViewModel.deleteComment(
                requireContext(),
                arguments.articleId,
                commentId,
                currentUser.token,
                position,
                commentsAdapter,
                fcmToken
            )
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negativeButton)) { _, _ ->
            alertDialogBuilder.setCancelable(true)
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun noInternetConnectionBehavior() {
        binding.apply {
            tryAgainBtn.setOnClickListener {
                showArticle()
                noInternet.isVisible = false
                refreshComments.isVisible = true
            }
        }
    }

    // TODO: To be implemented in the future
    private fun initUserClicks(article: Article) {
        binding.apply {
            avatarImage.setOnClickListener {
                fullScreenAvatar(
                    article.user?.url,
                    article.user?.name
                )
            }

            articleImage.setOnClickListener {
                fullScreenArticleAttachments(article.attachments?.last()?.resource_url)
            }

            likeArticle.setOnClickListener {
                if (currentUser.token.isEmpty()) {
                    AlertDialogs.createAccount(requireContext(), "للإعجاب أنشئ حساب أولا")
                } else {
                    if (article.liked) {
                        unLikeArticle(currentUser.token, article)
                    } else {
                        likeArticle(currentUser.token, article)
                    }
                }
            }

            addComment.setOnClickListener {
                showKeyboard(requireContext().applicationContext, binding.commentEdt)
            }

            shareArticle.setOnClickListener {
                val userName = article.user?.name
                val articleDescription = article.description

                val shareItem = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "$userName \n $articleDescription \n ${getString(R.string.aghour_share_content)}"
                    )
                    type = "text/plain"
                }
                requireContext().startActivity(Intent.createChooser(shareItem, "شارك الخبر.."))
            }
        }
    }

    private fun likeArticle(userToken: String, article: Article) {
        article.liked = true

        val request = likeApi.likeArticle(article.id, userToken)

        request.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {}
            override fun onFailure(call: Call<Article>, t: Throwable) {}
        })
    }

    private fun unLikeArticle(userToken: String, article: Article) {
        article.liked = false
        val request = likeApi.unLikeArticle(article.id, userToken)
        request.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {}
            override fun onFailure(call: Call<Article>, t: Throwable) {}
        })
    }
}