package com.aghourservices.ui.fragment.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentAddArticleBinding
import com.aghourservices.ui.fragment.BaseFragment
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.main.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.showProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog
import com.aghourservices.utils.interfaces.AlertDialog.Companion.createAccount
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddArticleFragment : BaseFragment() {
    private lateinit var binding: FragmentAddArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddArticleBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        hideUserAddData()
        hideToolbar()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (binding.textArticle.requestFocus()) {
            ShowSoftKeyboard.show(requireActivity(), binding.textArticle)
        }

        binding.sendArticle.setOnClickListener {
            showProgressDialog(requireContext())
            val article = Article()
            article.description = binding.textArticle.text.toString()

            if (article.inValid()) {
                binding.textArticle.error = "أكتب الخبر أولا"
                hideProgressDialog()
            } else {
                createArticle(article)
            }
        }

        binding.btnRegister.setOnClickListener {
            createAccount(requireActivity())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigation()
        showToolbar()
    }

    private fun createArticle(article: Article) {
        val user = getUserData(requireActivity())
        val retrofitBuilder = activity?.let {
            RetrofitInstance().newsApi.createArticle(
                article.toJsonObject(),
                user.token
            )
        }
        retrofitBuilder?.enqueue(object : Callback<Article> {
            override fun onResponse(
                call: Call<Article>,
                response: Response<Article>
            ) {
                AlertDialog.dataAdded(requireContext())
                setTextEmpty()
            }

            override fun onFailure(
                call: Call<Article>,
                t: Throwable
            ) {
                AlertDialog.noInternet(requireContext())
                hideProgressDialog()
            }
        })
    }

    private fun setTextEmpty() {
        binding.textArticle.text!!.clear()
        hideProgressDialog()
    }

    private fun hideUserAddData() {
        val isUserLogin = isUserLoggedIn(requireActivity())
        if (isUserLogin) {
            binding.sendArticle.visibility = View.VISIBLE
        } else {
            binding.sendArticle.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }
}