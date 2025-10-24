package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.Constant
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityContactandSupportBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.ContactSupportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class ContactandSupportActivity : BaseActivity() {
    private lateinit var binding: ActivityContactandSupportBinding
    private val viewModel: ContactSupportViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactandSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.start()
        screenOpened("ContactSupport")
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.edttxtDescription.addTextChangedListener {

            binding.txtcount.text = "" + binding.edttxtDescription.text.toString().length + "/200"

        }

        binding.btnSubmit.setOnClickListener {
            if (binding.edttxtname.text.toString().trim() == "") {
                showToast("Please Enter Name")

            } else if (binding.edttxtEmailAddress.text.toString().trim().isEmpty()) {
                showToast("Please Enter Email")
            } else if (!Constant.checkEmail(binding.edttxtEmailAddress.text.toString().trim())) {
                showToast("Please Enter Valid Email")

            } else if (binding.edttxtDescription.text.toString().trim() == "") {
                showToast("Please Enter Description")

            } else {
                viewModel.callApiData(
                    binding.edttxtname.text.toString().trim(),
                    binding.edttxtEmailAddress.text.toString().trim(),
                    binding.edttxtDescription.text.toString().trim()
                )
            }

        }

        Lisner()
    }

    private fun Lisner() {
        lifecycleScope.launch {
            viewModel.contactSupportConversion.collect {
                when (it) {
                    ContactSupportViewModel.ContactSupportEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is ContactSupportViewModel.ContactSupportEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                    }

                    is ContactSupportViewModel.ContactSupportEvent.Loading -> {
                        showProgressBar()

                    }

                    is ContactSupportViewModel.ContactSupportEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {

                            showToast("Submit Successfully")
                            onBackPressed()

                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }
    }
}


