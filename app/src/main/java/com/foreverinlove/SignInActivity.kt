package com.foreverinlove

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.databinding.ActivitySignInBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.screen.activity.AddProfilePictureActivity
import com.foreverinlove.screen.activity.BioScreenActivity
import com.foreverinlove.screen.activity.FaqActivity
import com.foreverinlove.screen.activity.OtpActivity
import com.foreverinlove.screen.activity.RegisterFlowStatus
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    private var binding: ActivitySignInBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        checkIfUserIsInRegisterFlow()

        val s =
            "By signing in, you agree to \nForeEverUs In Love \n\nPrivacy policy \nand \nTerms & Conditions"
        val ss = SpannableString(s)

        val first = "Privacy policy"
        val second = "Terms & Conditions"

        val firstIndex = s.indexOf(first)
        val secondIndex = s.indexOf(second)
        val firstWordClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(
                    Intent(this@SignInActivity, FaqActivity::class.java).putExtra(
                        "type",
                        "pricy"
                    )
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val secondWordClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(
                    Intent(this@SignInActivity, FaqActivity::class.java).putExtra(
                        "type",
                        "term"
                    )
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(
            firstWordClick,
            firstIndex,
            firstIndex + first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            secondWordClick,
            secondIndex,
            secondIndex + second.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding?.txtTerms?.linksClickable = true
        binding?.txtTerms?.movementMethod = LinkMovementMethod.getInstance()
        binding?.txtTerms?.setText(ss, TextView.BufferType.SPANNABLE)
        binding?.txtTerms?.highlightColor = Color.TRANSPARENT

        //keyboard mathij button ne click kri skay
        binding?.etxtPhone?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding?.btnContinue?.performClick()
                return@OnEditorActionListener true
            }
            false
        })

        binding?.btnContinue?.setOnClickListener {
            if (dataValid()) {
                viewModel.getLoginStatus(binding?.etxtCountryCode?.textView_selectedCountry?.text.toString() + binding?.etxtPhone?.text.toString())
            }
        }


        lifecycleScope.launch {

            viewModel.loginConversion.collect {
                when (it) {
                    LoginViewModel.GetLoginEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is LoginViewModel.GetLoginEvent.Failure -> {
                        Utility.hideProgressBar()
                    }
                    LoginViewModel.GetLoginEvent.Loading -> {
                        showProgressBar()
                    }
                    is LoginViewModel.GetLoginEvent.Success -> {
                        Utility.hideProgressBar()

                        showToast(it.result.data?.login_otp ?: "")
                        //  showToast("Otp verified successfully")

                        val intent = Intent(this@SignInActivity, OtpActivity::class.java)
                        intent.putExtra("number", binding?.etxtPhone?.text.toString())
                        intent.putExtra(
                            "code",
                            binding?.etxtCountryCode?.textView_selectedCountry?.text.toString()
                        )
                        startActivity(intent)

                    }
                }
            }

        }

    }

    private fun checkIfUserIsInRegisterFlow() = lifecycleScope.launch {
        val userData = dataStoreGetUserData().firstOrNull()
        if (userData?.registerFlowStatus == RegisterFlowStatus.CreateProfile)
            startActivity(Intent(this@SignInActivity, BioScreenActivity::class.java))
        else if (userData?.registerFlowStatus == RegisterFlowStatus.Bio)
            startActivity(Intent(this@SignInActivity, AddProfilePictureActivity::class.java))
    }


    private fun dataValid(): Boolean {

        if (binding?.etxtPhone?.text.toString().isEmpty()) {
            showToast("Please Enter Phone Number")
            return false
        } else
            if (binding?.etxtPhone?.text?.length!! < 4) {
                showToast("Phone Number Is Invalid")
                return false
            }
        if (binding?.etxtPhone?.text?.length!! > 20) {
            showToast("Phone Number Is Invalid")
            return false
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }
}