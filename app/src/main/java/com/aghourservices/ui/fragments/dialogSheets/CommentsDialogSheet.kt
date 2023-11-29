package com.aghourservices.ui.fragments.dialogSheets

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
import com.aghourservices.data.model.Comment
import com.aghourservices.databinding.CommentsDialogSheetBinding
import com.aghourservices.ui.adapters.CommentsAdapter
import com.aghourservices.ui.viewModels.CommentsViewModel
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import com.aghourservices.utils.services.cache.UserInfo.getUserData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CommentsDialogSheet : BottomSheetDialogFragment() {
    private var _binding: CommentsDialogSheetBinding? = null
    private val binding get() = _binding!!
    private var behavior: BottomSheetBehavior<*>? = null
    private val arguments: CommentsDialogSheetArgs by navArgs()
    private val commentsViewModel: CommentsViewModel by viewModels()
    private val currentUser by lazy { getUserData(requireContext()) }
    private val fcmToken by lazy { getFCMToken(requireContext()) }
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
            isHideable = false
            isDraggable = false
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
            fcmToken
        )
        commentsViewModel.commentsLiveData.observe(viewLifecycleOwner) {
            commentsAdapter.setComments(it)
            hideProgressBar()
            binding.noComments.isVisible = it.isEmpty()
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            progressBar.isVisible = false
            commentsRecyclerView.isVisible = true
        }
    }

    private fun onCommentClick(v: View, position: Int) {
        val comment = commentsAdapter.getComment(position)
        val user = commentsAdapter.getComment(position).user!!

        val userProfile = CommentsDialogSheetDirections.actionCommentsDialogSheetToUserProfileFragment(
            user.id!!
        )
        val updateComment =
            CommentsDialogSheetDirections.actionCommentsDialogSheetToUpdateCommentFragment(
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

            R.id.user_name -> {
                findNavController().navigate(userProfile)
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
}