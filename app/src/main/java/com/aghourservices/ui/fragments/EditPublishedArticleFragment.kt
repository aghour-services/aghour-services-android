package com.aghourservices.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aghourservices.data.model.Article
import com.aghourservices.data.network.RetrofitInstance.articlesApi
import com.aghourservices.databinding.FragmentEditPublishedArticleBinding
import com.aghourservices.utils.helper.Intents.showKeyboard
import com.aghourservices.utils.helper.AlertDialogs
import com.aghourservices.utils.services.cache.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPublishedArticleFragment : BaseFragment() {
    private var _binding: FragmentEditPublishedArticleBinding? = null
    private val binding get() = _binding!!
    private val arguments: EditPublishedArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPublishedArticleBinding.inflate(layoutInflater)
        requireActivity().title = "تعديل الخبر"
        initScreenView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard(requireContext(), binding.root)
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
        val userDetails = UserInfo.getUserData(requireContext())
        article.description = binding.articleTv.text.toString().trim()

        val retrofitBuilder = articlesApi.updateArticle(
            arguments.articleId,
            userDetails.token,
            article.toJsonObject(),
            UserInfo.getFCMToken(requireContext())
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
        showBottomNavigation()
    }
}