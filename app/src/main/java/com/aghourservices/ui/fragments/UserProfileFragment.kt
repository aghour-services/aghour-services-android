package com.aghourservices.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.aghourservices.R
import com.aghourservices.data.model.User
import com.aghourservices.data.network.RetrofitInstance
import com.aghourservices.databinding.FragmentUserProfileBinding
import com.aghourservices.ui.activities.FullScreenProfileActivity
import com.aghourservices.utils.helper.Intents.loadProfileImage
import com.aghourservices.utils.services.cache.UserInfo.getFCMToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileFragment : BaseFragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val arguments: UserProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        getProfile()
    }

    private fun getProfile() {
        val retrofitInstance =
            RetrofitInstance.userApi.show(arguments.id, getFCMToken(requireContext()))

        retrofitInstance.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()

                binding.userName.apply {
                    text = user?.name
                    if (user?.verified == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tooltipText = context.getString(R.string.verified)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                    visibility = View.VISIBLE
                }

                loadProfileImage(requireContext(), user?.url, binding.avatarImage)

                requireActivity().title = user?.name
                userClicks()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "لا يوجد إنترنت", Toast.LENGTH_SHORT).show()
                userClicks()
            }
        })
    }

    private fun userClicks() {
        binding.apply {
            avatarImage.setOnClickListener {
                val bundle = Bundle().apply { putInt("id", arguments.id) }
                val intent =
                    Intent(requireActivity(), FullScreenProfileActivity::class.java).apply {
                        putExtras(bundle)
                    }
                startActivity(intent)
            }

            userName.setOnClickListener {
                val bundle = Bundle().apply { putInt("id", arguments.id) }
                val intent = Intent(requireActivity(), FullScreenProfileActivity::class.java).apply {
                    putExtras(bundle)
                }
                startActivity(intent, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigation()
    }
}