package com.foreverinlove.screen.activity
// fazar barrikab hatta iza astumuhum

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.foreverinlove.Constant
import com.foreverinlove.R
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityMainBinding
import com.foreverinlove.groupvideocall.GroupVideoCallActivity
import com.foreverinlove.network.Utility
import com.foreverinlove.singlevideocall.IncomingCallActivity
import com.foreverinlove.utility.NotificationFlowHandler
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.findNavController


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private  var binding: ActivityMainBinding?=null
    private lateinit var navController: NavController
    private val notificationFlowHandler : NotificationFlowHandler by lazy {
        NotificationFlowHandler().apply {
            fetchNormalNotificationData(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        navController = this.findNavController(R.id.host_fragment)

        val data=intent.getStringExtra("openProfile")



        val likesAndViewsMenuItem = binding!!.btmNavigtion.menu.findItem(R.id.likesAndViewsFragment)
        val discoverMenuItem = binding!!.btmNavigtion.menu.findItem(R.id.newDiscoverFragment)
        val groupBoardMenuItem = binding!!.btmNavigtion.menu.findItem(R.id.groupBoardFragment)
        val convertionMenuItem = binding!!.btmNavigtion.menu.findItem(R.id.newConverationFragment)
        val profileMenuItem = binding!!.btmNavigtion.menu.findItem(R.id.profileFragment)
        likesAndViewsMenuItem.apply {
            val spannableString = SpannableString(title)
            spannableString.setSpan(RelativeSizeSpan(0.73f), 0, spannableString.length, 0)
            title = spannableString
        }
        discoverMenuItem.apply {
            val spannableString = SpannableString(title)
            spannableString.setSpan(RelativeSizeSpan(0.70f), 0, spannableString.length, 0)
            title = spannableString
        }
        groupBoardMenuItem.apply {
            val spannableString = SpannableString(title)
            spannableString.setSpan(RelativeSizeSpan(0.70f), 0, spannableString.length, 0)
            title = spannableString
        }
        convertionMenuItem.apply {
            val spannableString = SpannableString(title)
            spannableString.setSpan(RelativeSizeSpan(0.70f), 0, spannableString.length, 0)
            title = spannableString
        }
        profileMenuItem.apply {
            val spannableString = SpannableString(title)
            spannableString.setSpan(RelativeSizeSpan(0.70f), 0, spannableString.length, 0)
            title = spannableString
        }

        NavigationUI.setupWithNavController(binding!!.btmNavigtion, navController)


        if(data=="yes"){
            navController.navigate(R.id.profileFragment)
        }

        when(notificationFlowHandler.flowTracker){
            is NotificationFlowHandler.FlowTracker.FirstTimeGroupVideoCall -> {
                val newIntent = Intent(this@MainActivity, GroupVideoCallActivity::class.java)
                startActivity(notificationFlowHandler.applyNotificationData(newIntent))
            }
            is NotificationFlowHandler.FlowTracker.FirstTimeSingleVideoCall -> {
                val newIntent = Intent(this@MainActivity, IncomingCallActivity::class.java)
                startActivity(notificationFlowHandler.applyNotificationData(newIntent))
            }
            null -> Unit
        }

    }


    override fun onBackPressed() {

        super.onBackPressed()

        if(Constant.lastFilterFragCloseTime==0L){
            Constant.lastFilterFragCloseTime = System.currentTimeMillis() - 5000
        }

        val filterTime = Constant.lastFilterFragCloseTime
        val currentTime = System.currentTimeMillis()

        if (isFinishing || Constant.isDiscoverOpen) {
            if((currentTime-filterTime)>2000) finishAffinity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding=null
    }

    override fun onStart() {
        super.onStart()
        resetBadgeCounterOfPushMessages()
    }

    private fun resetBadgeCounterOfPushMessages() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }



}