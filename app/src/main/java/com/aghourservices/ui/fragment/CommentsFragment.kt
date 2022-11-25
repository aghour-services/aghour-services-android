package com.aghourservices.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.data.model.Comment
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentCommentsBinding
import com.aghourservices.ui.adapter.CommentsAdapter
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.viewModel.CommentsViewModel
import com.aghourservices.utils.helper.ProgressDialog
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsFragment : BaseFragment(), ShowSoftKeyboard {
    private lateinit var binding: FragmentCommentsBinding
    private lateinit var commentList: ArrayList<Comment>
    private lateinit var commentsViewModel: CommentsViewModel
    private lateinit var commentsAdapter: CommentsAdapter

    private val args: CommentsFragmentArgs by navArgs()
    private var articleId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "إضافة تعليق"
        hideBottomNavigation()
        initRecyclerView()
        articleId = args.articleId

        setUpViewModel()
        binding.postComment.setOnClickListener {
            val comment = Comment(
                396, binding.commentTv.text.toString(), UserInfo.getUserData(requireContext()).name
            )
            comment.body = binding.commentTv.text.toString()

            if (comment.inValid()) {
                binding.commentTv.error = "أكتب تعليق"
            } else {
                createComment(comment)
            }
        }
        if (binding.commentTv.requestFocus()) {
            showKeyboard(requireContext(), binding.commentTv)
        }
    }

    private fun createComment(comment: Comment) {
        val user = UserInfo.getUserData(requireContext())

        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.postComment(
            articleId,
            user.token,
            comment.toJsonObject(),
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.d("add-comment", "onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("add-comment", "onFailure: ${t.message}")
            }
        })
    }

    private fun setTextEmpty() {
        binding.commentTv.text!!.clear()
        ProgressDialog.hideProgressDialog()
    }

    private fun initRecyclerView() {
        binding.apply {
            commentsRecyclerView.setHasFixedSize(true)
            commentsRecyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setUpViewModel() {
        commentsViewModel = ViewModelProvider(this)[CommentsViewModel::class.java]
        activity?.let { commentsViewModel.loadComments(it, articleId) }
        commentsViewModel.commentsLivewData.observe(viewLifecycleOwner) {
            commentList = it
            commentsAdapter = CommentsAdapter(requireContext(), commentList)
            binding.commentsRecyclerView.adapter = commentsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigation()
    }
}