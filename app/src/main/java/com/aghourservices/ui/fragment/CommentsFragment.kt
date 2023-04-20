package com.aghourservices.ui.fragment

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
import com.aghourservices.data.request.RetrofitInstance.newsApi
import com.aghourservices.databinding.FragmentCommentsBinding
import com.aghourservices.ui.adapter.CommentsAdapter
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.main.cache.UserInfo.getFCMToken
import com.aghourservices.ui.viewModel.CommentsViewModel
import com.aghourservices.utils.interfaces.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsFragment : BaseFragment() {
    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private val commentsViewModel: CommentsViewModel by viewModels()
    private val arguments: CommentsFragmentArgs by navArgs()
    private val user by lazy { UserInfo.getUserData(requireContext()) }
    private val commentsAdapter =
        CommentsAdapter { view, position -> onCommentItemClick(view, position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        requireActivity().title = "التعليقات"
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
    }

    override fun onResume() {
        super.onResume()
        loadComments()
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
                        if (user.token.isEmpty()) {
                            AlertDialog.createAccount(requireContext(), "للتعليق أنشئ حساب أولا")
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

    private fun showArticle() {
        val retrofitBuilder =
            newsApi.showArticle(arguments.articleId, user.token, getFCMToken(requireContext()))

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(
                call: Call<Article>, response: Response<Article>
            ) {
                if (response.isSuccessful) {
                    val article = response.body()
                    binding.apply {
                        articleUserName.apply {
                            text = article?.user?.name
                            if (article?.user?.is_verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
    }

    private fun postComment(comment: Comment) {
        showCommentProgressBar()
        commentsViewModel.addComment(
            requireContext(),
            arguments.articleId,
            user.token,
            commentsAdapter,
            comment
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
            getFCMToken(requireContext())
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
        val updateComment =
            CommentsFragmentDirections.actionCommentsFragmentToUpdateCommentFragment(
                arguments.articleId,
                comment.id,
                comment.body,
                comment.user?.name.toString()
            )
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
                user.token,
                position,
                commentsAdapter
            )
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negativeButton)) { _, _ ->
            alertDialogBuilder.setCancelable(true)
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}