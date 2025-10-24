package com.foreverinlove.screen.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.PlaybackParams
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.SignInActivity
import com.foreverinlove.chatmodual.BaseFragment
import com.foreverinlove.databinding.FragmentProfileBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.screen.activity.*
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreSetUserData
import com.foreverinlove.viewmodels.ProfileShowViewmodel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
@SuppressLint("SuspiciousIndentation")
class ProfileFragment : BaseFragment(R.layout.fragment_profile), ProfileShowViewmodel.OnDataGet {
    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileShowViewmodel by viewModels()
    private lateinit var firabseauth: FirebaseAuth


    private var isFirstTime = true
    override fun onResume() {
        super.onResume()

        if (!isFirstTime)
            viewModel.getData()

        isFirstTime = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        screenOpened("ProfileTab")
        viewModel.start(this)

        firabseauth = FirebaseAuth.getInstance()





        binding.edit.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.setting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingProfileActivity::class.java))
        }

        binding.subscribe.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    SubscriptionPlanActivity::class.java
                ).putExtra("type", "active")
            )
        }
        binding.notification.setOnClickListener {
            startActivity(Intent(requireContext(), NotifitcationActivity::class.java))
        }

        binding.superlike.setOnClickListener {
            startActivity(Intent(requireContext(), SuperLikeActivity::class.java))
        }

        binding.logout.setOnClickListener {
            successLogOutDialog("logout")
        }

        binding.delete.setOnClickListener {
            successLogOutDialog("delete")
        }

        lifecycleScope.launch {
            viewModel.viewedMeListConversion.collect {
                when (it) {
                    ProfileShowViewmodel.ProfileFieldResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is ProfileShowViewmodel.ProfileFieldResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        requireActivity().showToast(it.errorText)

                    }

                    is ProfileShowViewmodel.ProfileFieldResponseEvent.Loading -> {
                        requireActivity().showProgressBar()

                    }

                    is ProfileShowViewmodel.ProfileFieldResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            //viewModel.getdata()
                        } else if (it.result.status == -2) {
                            requireActivity().handleSessionExpired()
                        }

                    }
                }
            }
        }


    }

    private fun successLogOutDialog(flow:String) {

        val dialog = Dialog(requireContext(), R.style.successfullDailog)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.logout
                )

            )

        )
        val txtText= dialog.findViewById<TextView>(R.id.txt_logout)
        val btnText= dialog.findViewById<MaterialButton>(R.id.btnLogout)
        if(flow=="logout"){
            txtText.text="Are you sure you want to logout?"
            btnText.text="Logout"

        }else{
            txtText.text="Are you sure you want to delete account?"
            btnText.text="Delete"

        }
        //video mate
        val videoView = dialog.findViewById<VideoView>(R.id.videoView)

        val video = "android.resource://" + requireActivity().packageName + "/" + R.raw.logoutscreen
        val uri = Uri.parse(video)
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false // optional: loop video

            val myPlayBackParams = PlaybackParams()
            lifecycleScope.launch {
                myPlayBackParams.speed = 1f
                delay(10000)
                myPlayBackParams.speed = 0.5f

            }

            mediaPlayer.playbackParams = myPlayBackParams

            videoView.start() // start playing the video automatically
        }

        dialog.findViewById<AppCompatButton>(R.id.btnLogout).setOnClickListener {
            dialog.dismiss()

            lifecycleScope.launch {
                val job = launch {
                    viewModel.logoutApiCall()
                    // requireActivity().dataStoreClearAll()
                    viewModel.tempUserDataObject?.id = ""
                    viewModel.tempUserDataObject?.first_name = ""
                    viewModel.tempUserDataObject?.last_name = ""
                    viewModel.tempUserDataObject?.dob = ""
                    viewModel.tempUserDataObject?.age = ""
                    viewModel.tempUserDataObject?.email = ""
                    viewModel.tempUserDataObject?.gender = ""
                    viewModel.tempUserDataObject?.intrested = ""
                    viewModel.tempUserDataObject?.job_title = ""
                    viewModel.tempUserDataObject?.google_id = ""
                    viewModel.tempUserDataObject?.fb_id = ""
                    viewModel.tempUserDataObject?.apple_id = ""
                    viewModel.tempUserDataObject?.login_type = ""
                    viewModel.tempUserDataObject?.otp_expird_time = ""
                    viewModel.tempUserDataObject?.address = ""
                    viewModel.tempUserDataObject?.latitude = ""
                    viewModel.tempUserDataObject?.longitude = ""
                    viewModel.tempUserDataObject?.height = ""
                    viewModel.tempUserDataObject?.emailVerified = ""
                    viewModel.tempUserDataObject?.imageUrl1 = ""
                    viewModel.tempUserDataObject?.imageUrl2 = ""
                    viewModel.tempUserDataObject?.imageUrl3 = ""
                    viewModel.tempUserDataObject?.imageUrl4 = ""
                    viewModel.tempUserDataObject?.imageUrl5 = ""
                    viewModel.tempUserDataObject?.imageUrl6 = ""
                    viewModel.tempUserDataObject?.imageId1 = ""
                    viewModel.tempUserDataObject?.imageId2 = ""
                    viewModel.tempUserDataObject?.imageId3 = ""
                    viewModel.tempUserDataObject?.imageId4 = ""
                    viewModel.tempUserDataObject?.imageId5 = ""
                    viewModel.tempUserDataObject?.imageId6 = ""

                    viewModel.tempUserDataObject?.profile_video = ""
                    viewModel.tempUserDataObject?.lastseen = ""
                    viewModel.tempUserDataObject?.fcm_token = ""
                    viewModel.tempUserDataObject?.token = ""

                    viewModel.tempUserDataObject?.let { context?.dataStoreSetUserData(it) }
                    // signOutAuth()

                }
                job.join()
                startActivity(Intent(requireContext(), SignInActivity::class.java))

            }
        }

        dialog.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun signOutAuth() {
        firabseauth.signOut()
        //firabseauth.signInAnonymously()
    }

    @SuppressLint("SetTextI18n")
    override fun onGet(tempData: TempUserDataObject) {
        Log.d("TAG", "onGetasdasdas: " + tempData)
        binding.tvUserName.text = tempData.first_name + ", "
        binding.tvUserNameAge.text = tempData.age
        binding.location.text = "" + tempData.address
        Glide.with(requireContext()).load(tempData.imageUrl1).into(binding.userimage)
    }

}