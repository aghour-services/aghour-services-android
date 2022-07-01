package com.aghourservices.ui.news.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.aghourservices.R
import com.aghourservices.data.request.RetrofitInstance
import com.aghourservices.databinding.FragmentAddArticleBinding
import com.aghourservices.utils.interfaces.AlertDialog
import com.aghourservices.ui.app.cache.UserInfo
import com.aghourservices.ui.app.main.fragment.BaseFragment
import com.aghourservices.ui.app.user.SignUpActivity
import com.aghourservices.utils.helper.ProgressDialog.hideProgressDialog
import com.aghourservices.utils.helper.ProgressDialog.showProgressDialog
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
        hideUserAddData()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.hide()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (binding.textArticle.requestFocus()) {
            ShowSoftKeyboard.show(requireActivity(), binding.textArticle)
        }

        binding.sendArticle.setOnClickListener {
            showProgressDialog(requireContext())
            val article = com.aghourservices.data.model.Article()
            article.description = binding.textArticle.text.toString()

            if (article.inValid()) {
                binding.textArticle.error = "أكتب الخبر أولا"
                hideProgressDialog()
            } else {
                createArticle(article)
            }
        }

        binding.btnRegister.setOnClickListener {
            showDialog()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val activity = activity as AppCompatActivity
        activity.supportActionBar?.show()
    }

    private fun showDialog() {
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

    private fun createArticle(article: com.aghourservices.data.model.Article) {
        val user = UserInfo().getUserData(requireActivity())
        val retrofitBuilder =
            activity?.let { RetrofitInstance(it).retrofit.create(com.aghourservices.data.api.NewsApi::class.java) }
        val retrofitData = retrofitBuilder?.createArticle(article.toJsonObject(), user.token)
        retrofitData?.enqueue(object : Callback<com.aghourservices.data.model.Article> {
            override fun onResponse(
                call: Call<com.aghourservices.data.model.Article>,
                response: Response<com.aghourservices.data.model.Article>
            ) {
                AlertDialog.dataAdded(requireContext())
                setTextEmpty()
            }

            override fun onFailure(
                call: Call<com.aghourservices.data.model.Article>,
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
        val isUserLogin = UserInfo().isUserLoggedIn(requireActivity())
        if (isUserLogin) {
            binding.sendArticle.visibility = View.VISIBLE
        } else {
            binding.sendArticle.visibility = View.GONE
            binding.btnRegister.visibility = View.VISIBLE
        }
    }
}