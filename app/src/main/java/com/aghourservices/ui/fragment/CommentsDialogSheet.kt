package com.aghourservices.ui.fragment

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
import com.aghourservices.databinding.CommentsDialogSheetBinding
import com.aghourservices.ui.adapter.CommentsAdapter
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.main.cache.UserInfo.getFCMToken
import com.aghourservices.ui.viewModel.CommentsViewModel
import com.aghourservices.utils.helper.Event
import com.aghourservices.utils.interfaces.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsDialogSheet : BottomSheetDialogFragment() {
    private var _binding: CommentsDialogSheetBinding? = null
    private val binding get() = _binding!!
    private var behavior: BottomSheetBehavior<*>? = null
    private val arguments: CommentsDialogSheetArgs by navArgs()
    private val commentsViewModel: CommentsViewModel by viewModels()
    private val commentsAdapter =
        CommentsAdapter { view, position -> onCommentClick(view, position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CommentsDialogSheetBinding.inflate(inflater, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        initRecyclerView()
        loadComments()
        navigateToLikesDialog()
        initCommentEdt()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProductSheet()
    }

    private fun initProductSheet() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomSheet!!).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isHideable = true
        }

        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    private fun initRecyclerView() {
        binding.commentsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = commentsAdapter
            likesCount()
        }
    }

    private fun likesCount() {
        val likesCount = arguments.likesCount
        binding.likesCount.text = likesCount.toString()
    }

    private fun loadComments() {
        commentsViewModel.loadComments(
            requireContext(),
            arguments.articleId,
            getFCMToken(requireContext())
        )
        commentsViewModel.commentsLivewData.observe(viewLifecycleOwner) {
            commentsAdapter.setComments(it)
            hideProgressBar()
            if (it.isEmpty()) {
                binding.noComments.isVisible = true
            }
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            progressBar.isVisible = false
            commentsRecyclerView.isVisible = true
        }
    }

    private fun navigateToLikesDialog() {
        val action = CommentsDialogSheetDirections.actionCommentsDialogSheetToLikesDialogSheet(
            arguments.articleId,
            arguments.likesCount
        )
        binding.likesCount.setOnClickListener {
            findNavController().navigate(action)
        }
    }

    private fun onCommentClick(v: View, position: Int) {
        val comment = commentsAdapter.getComment(position)

        when (v.id) {
            R.id.update_comment -> {
                val action =
                    CommentsDialogSheetDirections.actionCommentsDialogSheetToUpdateCommentFragment(
                        arguments.articleId,
                        comment.id,
                        comment.body,
                        comment.user?.name.toString()
                    )
                findNavController().navigate(action)
            }

            R.id.delete_comment -> {
                deleteCommentAlert(position)
            }
        }
    }

    private fun deleteComment(position: Int) {
        val userDetails = UserInfo.getUserData(requireContext())
        val commentId = commentsAdapter.getComment(position).id

        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.deleteComment(
            arguments.articleId,
            commentId,
            userDetails.token,
            getFCMToken(requireContext())
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    commentsAdapter.removeComment(position)
                    loadComments()
                    Toast.makeText(requireContext(), "تم مسح التعليق", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
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
                        val comment = Comment()
                        comment.body = commentTxt
                        postComment(comment)
                    }
                }
            }
        })
    }

    private fun postComment(comment: Comment) {
        val userDetails = UserInfo.getUserData(requireContext())
        val eventName = "Comment_Added"
        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.postComment(
            arguments.articleId,
            userDetails.token,
            comment.toJsonObject(),
            getFCMToken(requireContext())
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    binding.commentEdt.text.clear()
                    commentsAdapter.addComment(response.body()!!)
                    binding.noComments.isVisible = false
                    Event.sendFirebaseEvent(eventName, "")
                    loadComments()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
    }
}