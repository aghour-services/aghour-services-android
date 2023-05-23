package com.aghourservices.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aghourservices.data.model.Comment
import com.aghourservices.data.network.RetrofitInstance.commentsApi
import com.aghourservices.databinding.FragmentEditCommentBinding
import com.aghourservices.utils.helper.Intents.showKeyboard
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.services.cache.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCommentFragment : BaseFragment() {
    private var _binding: FragmentEditCommentBinding? = null
    private val binding get() = _binding!!
    private val arguments: EditCommentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCommentBinding.inflate(inflater, container, false)
        requireActivity().title = "تعديل التعليق"
        initScreenView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard(requireContext(), binding.commentTv)
        hideBottomNavigation()
    }

    private fun initScreenView() {
        binding.commentTv.setText(arguments.commentBody)
        binding.userName.text = arguments.commentUser

        binding.updateComment.setOnClickListener {
            updateComment()
            findNavController().navigateUp()
        }

        binding.cancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateComment() {
        val comment = Comment()
        val userDetails = UserInfo.getUserData(requireContext())
        comment.body = binding.commentTv.text.toString().trim()

        val retrofitBuilder = commentsApi.updateComment(
            arguments.articleId,
            arguments.commentId,
            userDetails.token,
            comment.toJsonObject(),
            UserInfo.getFCMToken(requireContext())
        )

        retrofitBuilder.enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) { }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                AlertDialogs.noInternet(requireContext())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}