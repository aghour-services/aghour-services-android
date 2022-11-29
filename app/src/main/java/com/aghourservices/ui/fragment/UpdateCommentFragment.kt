package com.aghourservices.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aghourservices.data.model.Comment
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentUpdateCommentBinding
import com.aghourservices.ui.main.cache.UserInfo
import com.aghourservices.utils.interfaces.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateCommentFragment : BaseFragment() {
    private var _binding: FragmentUpdateCommentBinding? = null
    private val binding get() = _binding!!
    private val arguments: UpdateCommentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        initScreenView()
        initUserClick()
    }

    private fun initScreenView() {
        binding.commentTv.setText(arguments.commentBody)
    }

    private fun initUserClick() {
        binding.updateComment.setOnClickListener {
            updateComment()
            val action = UpdateCommentFragmentDirections.actionUpdateCommentFragmentToCommentsFragment(
                    arguments.articleId)
            findNavController().navigate(action)
        }

        binding.cancelEdit.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateComment() {
        val comment = Comment()
        val userDetails = UserInfo.getUserData(requireContext())
        comment.body = binding.commentTv.text.toString()

        val retrofitBuilder = RetrofitInstance(requireContext()).commentsApi.updateComment(
            arguments.articleId,
            arguments.commentId,
            userDetails.token,
            comment.toJsonObject(),
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {

                if (response.isSuccessful) {
                    Log.d("edit", "onResponse: ${response.body()}")
                    Log.d("edit", "Updated Comment")
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("edit", "onFailure: ${t.message}")
                AlertDialog.noInternet(requireContext())
            }
        })
    }

    private fun hideUserDetails(){}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}