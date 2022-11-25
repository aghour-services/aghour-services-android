package com.aghourservices.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.data.model.Article
import com.aghourservices.data.model.Comment
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentCommentsBinding
import com.aghourservices.ui.adapter.ArticlesAdapter
import com.aghourservices.ui.adapter.CommentsAdapter
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.viewModel.CommentsViewModel
import com.aghourservices.ui.viewModel.NewsViewModel
import com.aghourservices.utils.helper.ProgressDialog
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsFragment : BaseFragment(), ShowSoftKeyboard {
    private lateinit var binding: FragmentCommentsBinding
    private lateinit var commentsViewModel: CommentsViewModel
    private lateinit var commentsAdapter: CommentsAdapter
    private val arguments: CommentsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "إضافة تعليق"
        hideBottomNavigation()
        setUpViewModel()
        initRecyclerView()

        binding.postComment.setOnClickListener {
            val comment = Comment()
            comment.body = binding.commentTv.text.toString()

            if (comment.inValid()) {
                binding.commentTv.error = "أكتب تعليق"
            } else {
                postComment(comment)
                commentsViewModel.loadComments(requireContext(), arguments.articleId)
            }
        }

        if (binding.commentTv.requestFocus()) {
            showKeyboard(requireContext(), binding.commentTv)
        }
    }

    private fun setUpViewModel() {
        commentsViewModel = ViewModelProvider(this)[CommentsViewModel::class.java]
        activity?.let { commentsViewModel.loadComments(it, arguments.articleId) }
        commentsViewModel.commentsLivewData.observe(viewLifecycleOwner) {
            commentsAdapter =
                CommentsAdapter(requireContext(), it)
            binding.commentsRecyclerView.adapter = commentsAdapter

        }
    }

    private fun initRecyclerView() {
        binding.commentsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun postComment(comment: Comment) {
        val user = getUserData(requireContext())

        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.postComment(
            arguments.articleId,
            user.token,
            comment.toJsonObject(),
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.d("data", "onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    Log.d("data", "onSuccess: ${response.body()}")
                    setTextEmpty()
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("data", "onFailure: ${t.message}")
            }
        })
    }

    private fun setTextEmpty() {
        binding.commentTv.text!!.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigation()
    }
}