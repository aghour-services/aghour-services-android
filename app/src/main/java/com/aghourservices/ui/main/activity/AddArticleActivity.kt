package com.aghourservices.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aghourservices.data.model.Article
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.ActivityAddArticleBinding
import com.aghourservices.ui.main.cache.UserInfo.getUserData
import com.aghourservices.ui.main.cache.UserInfo.isUserLoggedIn
import com.aghourservices.utils.helper.ProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.interfaces.AlertDialog
import com.aghourservices.utils.interfaces.ShowSoftKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddArticleActivity : AppCompatActivity(), ShowSoftKeyboard {
    private lateinit var binding: ActivityAddArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideUserAddData()
        initUserClick()
        showSoftKeyboard()
    }

    private fun showSoftKeyboard() {
        binding.textArticle.apply {
            requestFocus()
            showKeyboard(this@AddArticleActivity, this)
        }
    }

    private fun initUserClick() {
        binding.apply {
            backBtn.setOnClickListener {
                onBackPressed()
            }

            sendArticle.setOnClickListener {
                ProgressDialog.showProgressDialog(this@AddArticleActivity)
                val article = Article()
                article.description = binding.textArticle.text.toString()

                if (article.inValid()) {
                    binding.textArticle.error = "أكتب الخبر أولا"
                    hideProgressDialog()
                } else {
                    createArticle(article)
                }
            }

            btnRegister.setOnClickListener {
                AlertDialog.createAccount(this@AddArticleActivity)
            }
        }
    }

    private fun createArticle(article: Article) {
        val user = getUserData(this)
        val retrofitBuilder = RetrofitInstance(this).newsApi.createArticle(
            article.toJsonObject(),
            user.token
        )

        retrofitBuilder.enqueue(object : Callback<Article> {
            override fun onResponse(
                call: Call<Article>,
                response: Response<Article>
            ) {
                AlertDialog.dataAdded(this@AddArticleActivity)
                setTextEmpty()
            }

            override fun onFailure(
                call: Call<Article>,
                t: Throwable
            ) {
                AlertDialog.noInternet(this@AddArticleActivity)
                hideProgressDialog()
            }
        })
    }

    private fun setTextEmpty() {
        binding.textArticle.text!!.clear()
        hideProgressDialog()
    }

    private fun hideUserAddData() {
        val isUserLogin = isUserLoggedIn(this@AddArticleActivity)
        if (isUserLogin) {
            binding.sendArticle.visibility = View.VISIBLE
        } else {
            binding.sendArticle.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }
}