package com.foreverinlove.screen.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.adapter.NotificationListAdapter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityNotifitcationBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.*
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.GetNotificationListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotifitcationActivity : BaseActivity() {
    private lateinit var binding: ActivityNotifitcationBinding

    private val notificationViewModel: GetNotificationListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotifitcationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        notificationViewModel.start()
        screenOpened("NotificationList")

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            notificationViewModel.notificationListConversion.collect {
                when (it) {
                    GetNotificationListViewModel.NotificationListResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GetNotificationListViewModel.NotificationListResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        binding.notirecy.visibility = View.GONE
                    }
                    is GetNotificationListViewModel.NotificationListResponseEvent.Loading -> {
                        showProgressBar()

                        binding.notirecy.visibility = View.VISIBLE
                    }
                    is GetNotificationListViewModel.NotificationListResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data?.notifcation.isNullOrEmpty()) {
                                binding.notirecy.visibility = View.GONE
                                binding.emptyimg.visibility = View.VISIBLE
                            } else {
                                binding.notirecy.visibility = View.VISIBLE
                                binding.emptyimg.visibility = View.GONE
                                setNotificationListData(it.result.data?.notifcation ?: listOf())
                            }
                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }

    }


    private val listTop = ArrayList<Notifacation>()
    private lateinit var notificationListAdapter: NotificationListAdapter


    private fun setNotificationListData(data: List<Notifacation>) {
        listTop.clear()
        listTop.addAll(data)
        notificationListAdapter =
            NotificationListAdapter(this@NotifitcationActivity, data, userListener)
        binding.notirecy.adapter = notificationListAdapter

    }

    private val userListener = object : NotificationListAdapter.onClick {
        override fun itemClick(data: Notifacation, position: Int) {


        }

    }
}