package com.aghourservices.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aghourservices.data.model.Article
import com.aghourservices.data.network.RetrofitInstance
import com.aghourservices.databinding.FragmentEditDraftArticleBinding
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.services.cache.UserInfo
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditDraftArticleFragment : BaseFragment() {
    private var _binding: FragmentEditDraftArticleBinding? = null
    private val binding get() = _binding!!
    private val arguments: EditDraftArticleFragmentArgs by navArgs()
    private val user by lazy { UserInfo.getUserData(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDraftArticleBinding.inflate(inflater, container, false)

        requireActivity().title = "تعديل الخبر"
        initScreenView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
    }

    private fun initScreenView() {
        binding.articleTv.setText(arguments.articleDescription)
        binding.userName.text = arguments.articleUserName

        binding.updateArticle.setOnClickListener {
            updateArticle()
            findNavController().navigateUp()
        }

        binding.cancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateArticle() {
        val article = Article()
        article.status = "draft"
        val articleDescription = binding.articleTv.text.toString().trim()
        article.description = articleDescription

        val retrofitBuilder = RetrofitInstance.articlesApi.updateArticle(
            arguments.articleId,
            user.token,
            article.toJsonObject(),
            getFCMToken(requireContext())
        )

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) { }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                AlertDialogs.noInternet(requireContext())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}