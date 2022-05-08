package com.aghourservices.news.user_add_article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.BaseFragment
import com.aghourservices.cache.UserInfo
import com.aghourservices.constants.RetrofitInstance
import com.aghourservices.databinding.FragmentAddArticleBinding
import com.aghourservices.interfaces.AlertDialog
import com.aghourservices.news.api.Article
import com.aghourservices.news.api.CreateArticle
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
        hideBottomNav()
        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.hide()
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.sendArticle.setOnClickListener {
            val article = Article()
            article.description = binding.textArticle.text.toString()

            if (article.inValid()) {
                binding.textArticle.error = "أكتب الخبر أولا"
            } else {
                createArticle(article)
            }
        }
    }

    private fun createArticle(article: Article) {
        val user = UserInfo().getUserData(requireActivity())
        val retrofitBuilder =
            RetrofitInstance(requireActivity()).retrofit.create(CreateArticle::class.java)
        val retrofitData = retrofitBuilder.createArticle(article.toJsonObject(), user.token)
        retrofitData.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                AlertDialog.articleAdded(requireContext())
                setTextEmpty()
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
                AlertDialog.noInternet(requireContext())
            }
        })
    }

    private fun setTextEmpty() {
        binding.textArticle.text.clear()
    }
}