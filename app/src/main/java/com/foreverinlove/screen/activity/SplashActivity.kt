package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.ComplatedRegisterActivity
import com.foreverinlove.IdVerifyActivity
import com.foreverinlove.R
import com.foreverinlove.SignInActivity
import com.foreverinlove.databinding.ActivitySplashBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.NotificationFlowHandler
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.SplaseScreeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var tempUserDataObject: TempUserDataObject
    private val viewModel: SplaseScreeViewModel by viewModels()

    private val notificationFlowHandler: NotificationFlowHandler by lazy {
        NotificationFlowHandler().apply {
            fetchDataFirstTime(intent)
        }
    }

    private var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        //Glide.with(applicationContext).load(R.mipmap.splace1).into(binding!!.image1)
        //Glide.with(applicationContext).load(R.mipmap.splace2).into(binding!!.image2)
        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    Log.d("TAG", "onCdfgfgreate: "+it)
                    tempUserDataObject = it


                }
        }

        lifecycleScope.launch {
            try {
                viewModel.callApiData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val b = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.fade_transition
        )
        /* b.setAnimationListener(object : Animation.AnimationListener {
             override fun onAnimationStart(p0: Animation?) {
             }

             override fun onAnimationEnd(p0: Animation?) {
                 binding?.imgphase1?.visibility = View.GONE
             }

             override fun onAnimationRepeat(p0: Animation?) {
             }
         })
         binding?.imgphase1?.animation = b*/


        lifecycleScope.launch {
            viewModel.addtionalQueConversion.collect {
                when (it) {
                    SplaseScreeViewModel.ResponseEvent.Empty -> Utility.hideProgressBar()
                    is SplaseScreeViewModel.ResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                    }

                    is SplaseScreeViewModel.ResponseEvent.Loading -> {
                        //showProgressBar()

                    }

                    is SplaseScreeViewModel.ResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        callTemp()
                    }
                }
            }
        }

    }

    private fun callTemp() {
        CoroutineScope(Dispatchers.IO).launch {

            // delay(3000)

            Log.d("TAG", "callTemp:103 " + tempUserDataObject.first_name)
            Log.d("TAG", "callTemp:105 " + tempUserDataObject.email)
            Log.d("TAG", "callTemp:106 " + tempUserDataObject.token)
            Log.d("TAG", "callTemp:107 " + tempUserDataObject.imageUrl1)
            Log.d("TAG", "callTemp:116 " + tempUserDataObject.imageId1)
            Log.d("TAG", "callTemp:108 " + tempUserDataObject.emailVerified)
            Log.d("TAG", "callTemp:109 " + tempUserDataObject.registerFlowStatus)
            Log.d("TAG", "callTemp:121 " + tempUserDataObject.id_verification)

            if (tempUserDataObject.first_name != "" && tempUserDataObject.email != "" &&
                tempUserDataObject.token != "" && tempUserDataObject.imageUrl1 != "" &&
                (tempUserDataObject.emailVerified == null || tempUserDataObject.emailVerified != ("0")) && tempUserDataObject.registerFlowStatus == null
            ) {
                if(tempUserDataObject.id_verification.isEmpty()){
                    startActivity(Intent(this@SplashActivity, ComplatedRegisterActivity::class.java))
                  //  startActivity(Intent(this@SplashActivity, IdVerifyActivity::class.java))

                }else{
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(notificationFlowHandler.applyNotificationData(intent))
                }

            } else {
                startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            }
        }
    }


}