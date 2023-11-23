package com.aghourservices.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.databinding.FragmentNotificationsBinding
import com.aghourservices.ui.adapters.NotificationsAdapter
import com.aghourservices.ui.base.BaseFragment
import com.aghourservices.ui.viewModels.NotificationsViewModel

class NotificationsFragment : BaseFragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val notificationsAdapter =
        NotificationsAdapter { position -> onListItemClick(position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(layoutInflater)
        initRecyclerView()
        initNotificationObserve()
        handleOnBackPressed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        hideToolbar()
    }


    private fun handleOnBackPressed() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initNotificationObserve() {
        notificationsViewModel.getNotifications(requireContext(), fcmToken, currentUser.token)
        notificationsViewModel.notificationsLiveData.observe(viewLifecycleOwner) {
            notificationsAdapter.setNotifications(it)
            progressBar()
        }
    }

    private fun initRecyclerView() {
        binding.notificationsRv.apply {
            setHasFixedSize(true)
            adapter = notificationsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onListItemClick(position: Int) {
        val notification = notificationsAdapter.getNotification(position)
        val notifiableId = notification.notifiableId

        when (notification.notifiableType) {
            "Comment" -> {
                val articleId = notification.articleId

                val action =
                    NotificationsFragmentDirections.actionNotificationsFragmentToShowOneArticleFragment(
                        articleId!!, notifiableId
                    )
                findNavController().navigate(action)
            }

            "Article" -> {
                val action =
                    NotificationsFragmentDirections.actionNotificationsFragmentToShowOneArticleFragment(
                        notifiableId
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun progressBar() {
        binding.progressBar.visibility = View.GONE
        binding.notificationsRv.visibility = View.VISIBLE
    }
}