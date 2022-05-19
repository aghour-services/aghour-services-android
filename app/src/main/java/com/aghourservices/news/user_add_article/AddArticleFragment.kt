package com.aghourservices.news.user_add_article

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.aghourservices.BaseFragment
import com.aghourservices.R
import com.aghourservices.cache.UserInfo
import com.aghourservices.constants.RetrofitInstance
import com.aghourservices.databinding.FragmentAddArticleBinding
import com.aghourservices.interfaces.AlertDialog
import com.aghourservices.interfaces.ShowSoftKeyboard
import com.aghourservices.news.api.Article
import com.aghourservices.news.api.CreateArticle
import com.aghourservices.user.SignUpActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddArticleFragment : BaseFragment() {
    private lateinit var binding: FragmentAddArticleBinding
    private var progressDialog: Dialog? = null

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
        hideUserAddData()

        val activity = (activity as AppCompatActivity)
        activity.supportActionBar?.hide()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (binding.textArticle.requestFocus()) {
            ShowSoftKeyboard.show(requireActivity(), binding.textArticle)
        }

        binding.sendArticle.setOnClickListener {
            showProgressDialog()
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
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.create_account_first))
            alertDialogBuilder.setMessage(getString(R.string.should_create))
            alertDialogBuilder.setIcon(R.drawable.ic_launcher_round)
            alertDialogBuilder.setCancelable(true)
            alertDialogBuilder.setPositiveButton("إنشاء الان") { _, _ ->
                startActivity(Intent(requireActivity(), SignUpActivity::class.java))
                requireActivity().finish()
            }
            alertDialogBuilder.setNegativeButton(R.string.cancelButton) { _, _ -> }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).textSize = 14f
            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).textSize = 14f
        }
    }

    private fun createArticle(article: Article) {
        val user = UserInfo().getUserData(requireActivity())
        val retrofitBuilder = activity?.let { RetrofitInstance(it).retrofit.create(CreateArticle::class.java) }
        val retrofitData = retrofitBuilder?.createArticle(article.toJsonObject(), user.token)
        retrofitData?.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                AlertDialog.dataAdded(requireContext())
                setTextEmpty()
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {
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
        val isUserLogin = UserInfo().isUserLoggedIn(requireActivity())
        if (isUserLogin) {
            binding.sendArticle.visibility = View.VISIBLE
        } else {
            binding.sendArticle.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }

    private fun showProgressDialog() {
        progressDialog = Dialog(requireContext())
        progressDialog!!.setContentView(R.layout.dialog_custom_progress)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null)
            progressDialog!!.dismiss()
    }
}