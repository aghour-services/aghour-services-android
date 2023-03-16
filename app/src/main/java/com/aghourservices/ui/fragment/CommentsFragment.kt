package com.aghourservices.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.R
import com.aghourservices.data.model.Comment
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentCommentsBinding
import com.aghourservices.ui.adapter.CommentsAdapter
import com.aghourservices.ui.main.activity.SignInActivity
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.main.cache.UserInfo.getFCMToken
import com.aghourservices.ui.viewModel.CommentsViewModel
import com.aghourservices.utils.helper.Event.Companion.sendFirebaseEvent
import com.aghourservices.utils.interfaces.AlertDialog
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
        refresh()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        initArticleView()
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

    private fun initArticleView() {
        binding.apply {
            articleUserName.text = arguments.userName
            articleCreatedAt.text = arguments.time
            articleDescription.text = arguments.description
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
                            addComment(comment)
                        }
                    }
                }
            }
        })
    }

    private fun loadComments() {
        commentsViewModel.loadComments(requireContext(), arguments.articleId, getFCMToken(requireContext()))
        commentsViewModel.commentsLivewData.observe(viewLifecycleOwner) {
            commentsAdapter.setComments(it)
            stopShimmerAnimation()
            if (it.isEmpty()) {
                noComments()
            }
        }
    }

    private fun noComments() {
        binding.noComments.isVisible = true
        binding.commentsRecyclerView.isVisible = false
    }

    private fun initRecyclerView() {
        binding.commentsRecyclerView.apply {
            setHasFixedSize(true)
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun addComment(comment: Comment) {
        showProgressBar()
        val eventName = "Comment_Added"
        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.postComment(
            arguments.articleId,
            user.token,
            comment.toJsonObject(),
            getFCMToken(requireContext())
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {

                if (response.isSuccessful) {
                    binding.commentEdt.text!!.clear()
                    binding.noComments.isVisible = false
                    commentsAdapter.addComment(response.body()!!)
                    sendFirebaseEvent(eventName, "")
                    hideProgressBar()
                    loadComments()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                stopShimmerAnimation()
                AlertDialog.noInternet(requireContext())
                hideProgressBar()
            }
        })
    }

    private fun deleteComment(position: Int) {
        val commentId = commentsAdapter.getComment(position).id

        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.deleteComment(
            arguments.articleId,
            commentId,
            user.token,
            getFCMToken(requireContext())
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    commentsAdapter.removeComment(position)
                    Toast.makeText(requireContext(), "تم مسح التعليق", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
    }

    fun createAccount() {
        val alertDialogBuilder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        alertDialogBuilder.setTitle("حدث خطأ ما")
        alertDialogBuilder.setMessage("يرجى تسجيل الدخول مرة أخرى")
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("تمام") { _, _ ->
            logOut()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).textSize = 14f
    }

    private fun logOut() {
        sendFirebaseEvent("Sign_Out", "")
        UserInfo.clearUserData(requireContext())
        startActivity(Intent(requireContext(), SignInActivity::class.java))
    }

    private fun showProgressBar() {
        binding.apply {
            commentProgress.isVisible = true
            commentBtn.isVisible = false
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            commentProgress.isVisible = false
            commentBtn.isVisible = true
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
        val commentId = commentsAdapter.getComment(position).id
        val commentBody = commentsAdapter.getComment(position).body
        val commentUser = commentsAdapter.getComment(position).user?.name.toString()

        when (v.id) {
            R.id.update_comment -> {
                val action =
                    CommentsFragmentDirections.actionCommentsFragmentToUpdateCommentFragment(
                        arguments.articleId,
                        commentId,
                        commentBody,
                        commentUser
                    )
                findNavController().navigate(action)
            }

            R.id.delete_comment -> {
                deleteCommentAlert(position)
            }
        }
    }

    private fun deleteCommentAlert(position: Int) {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_comment))
        alertDialogBuilder.setMessage(getString(R.string.are_you_sure_to_delete_comment))
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            deleteComment(position)
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