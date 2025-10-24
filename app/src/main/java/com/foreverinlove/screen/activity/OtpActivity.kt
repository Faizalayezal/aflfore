package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.databinding.ActivityOtpBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.TextViewExt
import com.foreverinlove.viewmodels.OtpViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class OtpActivity : AppCompatActivity() {

    private val viewModel: OtpViewModel by viewModels()
    private lateinit var firabseauth: FirebaseAuth

    private var binding: ActivityOtpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val number = intent.getStringExtra("number") ?: ""
        val code = intent.getStringExtra("code") ?: ""
        firabseauth = FirebaseAuth.getInstance()

        binding?.txtcountry?.text = code
        binding?.phonenumber?.text = number



        binding?.pencil?.setOnClickListener {
            onBackPressed()
        }

        viewModel.start()

        TextViewExt.setOtpFlow(binding!!.otp1, binding!!.otp2, binding!!.otp3, binding!!.otp4)

        binding?.btnContinue?.setOnClickListener {

            if (dataValid()) {

                viewModel.getOtpStatus(tempOtp)
            }

        }



        lifecycleScope.launch {

            viewModel.OtpConversion.collect {
                when (it) {
                    OtpViewModel.GetOtpEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is OtpViewModel.GetOtpEvent.Failure -> {
                        showToast(it.errorText)

                        Utility.hideProgressBar()
                    }

                    OtpViewModel.GetOtpEvent.Loading -> {
                        showProgressBar()
                    }

                    is OtpViewModel.GetOtpEvent.Success -> {
                        Utility.hideProgressBar()

                        if (it.result.data?.user?.first_name != null && it.result.data.user.email != null
                            && it.result.data.user.first_name != "" && it.result.data.user.email != ""
                        ) {
                            firabseauth.signInAnonymously()
                            startActivity(Intent(this@OtpActivity, MainActivity::class.java))
                            finish()
                        } else {
                            startActivity(
                                Intent(
                                    this@OtpActivity,
                                    CreateProfileActivity::class.java
                                )
                            )
                        }
                        if (it.result.status == 0) {
                            showToast(it.result.message ?: "")

                        } else {
                            showToast("OTP Verified Successfully")


                        }


                    }
                }
            }

        }
        lifecycleScope.launch {
            viewModel.loginConversion.collect {
                when (it) {
                    OtpViewModel.GetLoginEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is OtpViewModel.GetLoginEvent.Failure -> {
                        showToast(it.errorText)

                        Utility.hideProgressBar()
                    }

                    OtpViewModel.GetLoginEvent.Loading -> {
                        showProgressBar()
                    }

                    is OtpViewModel.GetLoginEvent.Success -> {
                        Utility.hideProgressBar()
                        showToast(it.result.data?.login_otp ?: "")
                        if (it.result.message == "Success") {
                            showToast("OTP Resent Successfully")

                        }


                    }
                }
            }
        }

        binding?.txtResend?.setOnClickListener {
            viewModel.getResendOtp()
        }

    }

    private var tempOtp = ""
    private fun dataValid(): Boolean {

        if (binding?.otp1?.text.toString().isEmpty() ||
            binding?.otp2?.text.toString().isEmpty() ||
            binding?.otp3?.text.toString().isEmpty() ||
            binding?.otp4?.text.toString().isEmpty()
        ) {
            showToast("Please Enter OTP")
            return false
        }

        tempOtp =
            binding?.otp1?.text.toString() + binding?.otp2?.text.toString() + binding?.otp3?.text.toString() + binding?.otp4?.text.toString()

        return true
    }
}