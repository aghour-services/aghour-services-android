package com.aghourservices.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.viewModel.CommentsViewModel
import com.aghourservices.utils.helper.Event.Companion.sendFirebaseEvent
import com.aghourservices.utils.interfaces.AlertDialog
import com.aghourservices.utils.interfaces.HideSoftKeyboard
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsFragment : BaseFragment(), ShowSoftKeyboard {
    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private val commentsViewModel: CommentsViewModel by viewModels()
    private val arguments: CommentsFragmentArgs by navArgs()
    private val commentsAdapter =
        CommentsAdapter { view, position -> onCommentItemClick(view, position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "التعليقات"
        hideBottomNavigation()
        hideUserData()
        loadComments()
        initRecyclerView()
        initUserClick()
        refresh()
        showKeyboard(requireContext(), binding.commentTv)
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

    private fun initUserClick() {
        binding.postComment.setOnClickListener {
            showProgressBar()
            val comment = Comment()
            comment.body = binding.commentTv.text.toString()

            if (comment.inValid()) {
                binding.commentTv.error = "أكتب تعليق"
                hideProgressBar()
            } else {
                postComment(comment)
            }
        }

        binding.btnRegister.setOnClickListener {
            AlertDialog.createAccount(requireContext())
        }
    }

    private fun loadComments() {
        commentsViewModel.loadComments(requireContext(), arguments.articleId)
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
        binding.nestedCommentsScroll.isVisible = false
    }

    private fun initRecyclerView() {
        binding.commentsRecyclerView.apply {
            setHasFixedSize(true)
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun postComment(comment: Comment) {
        val userDetails = getUserData(requireContext())
        val eventName = "Comment_Added"
        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.postComment(
            arguments.articleId,
            userDetails.token,
            comment.toJsonObject(),
        )
        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {

                if (response.isSuccessful) {
                    setTextEmpty()
                    loadComments()
                    HideSoftKeyboard.hide(requireContext(), binding.commentTv)
                    binding.noComments.isVisible = false
                    hideProgressBar()
                    sendFirebaseEvent(eventName, "")
                } else if (response.code() == 401) {
                    createAccount()
                    hideProgressBar()
                } else {
                    hideProgressBar()
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
        val userDetails = getUserData(requireContext())
        val commentId = commentsAdapter.getComment(position).id

        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.deleteComment(
            arguments.articleId,
            commentId,
            userDetails.token,
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    loadComments()
                    Log.d("delete", "onResponse: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
    }

    fun createAccount() {
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext(),R.style.AlertDialogTheme)
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
        binding.commentProgress.isVisible = true
        binding.postComment.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        binding.commentProgress.isVisible = false
        binding.postComment.visibility = View.VISIBLE
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
            nestedCommentsScroll.isVisible = true
        }
    }

    private fun setTextEmpty() {
        binding.commentTv.text!!.clear()
    }

    private fun hideUserData() {
        val isUserLogin = UserInfo.isUserLoggedIn(requireContext())
        if (isUserLogin) {
            binding.commentsLayout.visibility = View.VISIBLE
            binding.btnRegister.visibility = View.GONE
        } else {
            binding.commentsLayout.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }

    private fun onCommentItemClick(v: View, position: Int) {
        val commentId = commentsAdapter.getComment(position).id
        val commentBody = commentsAdapter.getComment(position).body

        when (v.id) {
            R.id.update_comment -> {
                val action =
                    CommentsFragmentDirections.actionCommentsFragmentToUpdateCommentFragment(
                        arguments.articleId,
                        commentId,
                        commentBody
                    )
                findNavController().navigate(action)
            }

            R.id.delete_comment -> {
                deleteComment(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}