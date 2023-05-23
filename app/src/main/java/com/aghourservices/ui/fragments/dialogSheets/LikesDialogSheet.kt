package com.aghourservices.ui.fragments.dialogSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.data.model.User
import com.aghourservices.data.network.RetrofitInstance.likeApi
import com.aghourservices.databinding.LikesDialogSheetBinding
import com.aghourservices.ui.adapters.LikesAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LikesDialogSheet : BottomSheetDialogFragment() {
    private var _binding: LikesDialogSheetBinding? = null
    private val binding get() = _binding!!
    private var behavior: BottomSheetBehavior<*>? = null
    private val arguments: LikesDialogSheetArgs by navArgs()
    private val likesAdapter =
        LikesAdapter { view, position -> onUserClick(view, position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LikesDialogSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProductSheet()
        initRecyclerView()
        getLikes()
    }

    private fun initProductSheet() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomSheet!!).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isHideable = true
        }

        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    private fun initRecyclerView() {
        binding.usersRecyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = likesAdapter
            likesCount()
        }
    }

    private fun likesCount() {
        val likesCount = arguments.likesCount
        binding.likesCount.text = likesCount.toString()
    }

    private fun getLikes() {
        val retrofitBuilder = likeApi.getLikes(
            arguments.articleId,
        )

        retrofitBuilder.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>,
            ) {
                if (response.isSuccessful) {
                    val users = response.body()!!
                    likesAdapter.setUsers(users)
                    hideProgressBar()

                    if (users.isEmpty()) {
                        binding.noLikes.isVisible = true
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                com.aghourservices.utils.interfaces.AlertDialog.noInternet(requireContext())
            }
        })
    }

    private fun hideProgressBar() {
        binding.apply {
            progressBar.isVisible = false
        }
    }

    private fun onUserClick(view: View, position: Int) {}
}