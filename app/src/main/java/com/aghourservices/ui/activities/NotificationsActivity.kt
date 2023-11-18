package com.aghourservices.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghourservices.databinding.ActivityNotificationsBinding
import com.aghourservices.ui.adapters.NotificationsAdapter
import com.aghourservices.ui.viewModels.NotificationsViewModel
import com.aghourservices.utils.services.cache.UserInfo

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val notificationsAdapter =
        NotificationsAdapter { position -> onListItemClick(position) }
    val fcmToken by lazy { UserInfo.getFCMToken(this) }
    val userToken by lazy { UserInfo.getUserData(this).token }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        initNotificationObserve()
    }


    private fun initNotificationObserve() {
        notificationsViewModel.getNotifications(this, fcmToken, userToken)
        notificationsViewModel.notificationsLiveData.observe(this) {
            notificationsAdapter.setNotifications(it)
            progressBar()
        }
    }

    private fun initRecyclerView() {
        binding.notificationsRv.apply {
            setHasFixedSize(true)
            adapter = notificationsAdapter
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
        }
    }

    private fun onListItemClick(position: Int) {
        val notification = notificationsAdapter.getNotification(position)
        Toast.makeText(
            this,
            "${notification.notifiableId} ${notification.notifiableType}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun progressBar() {
        binding.progressBar.visibility = View.GONE
        binding.notificationsRv.visibility = View.VISIBLE
    }
}