package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityFaqBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.PagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class FaqActivity : BaseActivity() {
    private lateinit var binding: ActivityFaqBinding
    private lateinit var type: String
    private val viewModel: PagesViewModel by viewModels()
    var endIndex: Int? = 0

    var spannableString: SpannableString? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.start()
        type = intent.getStringExtra("type") ?: ""

        val iconDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.profile2)

        val spannableStringBuilder = SpannableStringBuilder("You can edit your profile by logging into your account and clicking on the \"Profile\" icon  you can update your photos,personal information, and preferences.")

        val iconStart = (spannableStringBuilder.length + 23) / 2
        iconDrawable?.let {
            it.setBounds(0, 0, 38, 38)
            spannableStringBuilder.setSpan(
                ImageSpan(it, ImageSpan.ALIGN_BASELINE),
                iconStart,
                iconStart + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.txt2.text = spannableStringBuilder


        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        Listner()

        when (type) {
            "faq" -> {
                binding.txtchange.text = "FAQ"
                binding.txtchange.isAllCaps = false
                screenOpened("Faq")
                binding.baseCardview.visibility = View.VISIBLE
                binding.baseCardview2.visibility = View.VISIBLE
                binding.baseCardview3.visibility = View.VISIBLE
                binding.baseCardview4.visibility = View.VISIBLE
                binding.baseCardview5.visibility = View.VISIBLE
                binding.baseCardview6.visibility = View.VISIBLE
                binding.baseCardview7.visibility = View.VISIBLE
                binding.baseCardview8.visibility = View.VISIBLE
                binding.baseCardview9.visibility = View.VISIBLE
                binding.nested.visibility = View.GONE
            }

            "pricy" -> {
                binding.txtchange.text = "Privacy Policy "
                binding.txtchange.isAllCaps = false
                screenOpened("PrivacyPolicy")
                binding.baseCardview.visibility = View.GONE
                binding.baseCardview2.visibility = View.GONE
                binding.baseCardview3.visibility = View.GONE
                binding.baseCardview4.visibility = View.GONE
                binding.baseCardview5.visibility = View.GONE
                binding.baseCardview6.visibility = View.GONE
                binding.baseCardview7.visibility = View.GONE
                binding.baseCardview8.visibility = View.GONE
                binding.baseCardview9.visibility = View.GONE
                binding.nested.visibility = View.VISIBLE
            }

            "term" -> {
                binding.txtchange.text = "Terms & Conditions"
                binding.txtchange.isAllCaps = false
                screenOpened("TermsAndCondition")
                binding.baseCardview.visibility = View.GONE
                binding.baseCardview2.visibility = View.GONE
                binding.baseCardview3.visibility = View.GONE
                binding.baseCardview4.visibility = View.GONE
                binding.baseCardview5.visibility = View.GONE
                binding.baseCardview6.visibility = View.GONE
                binding.baseCardview7.visibility = View.GONE
                binding.baseCardview8.visibility = View.GONE
                binding.baseCardview9.visibility = View.GONE
                binding.nested.visibility = View.VISIBLE
            }
        }

        binding.arrowButton.setOnClickListener {
            hiddview()
        }
        binding.arrowButton2.setOnClickListener {
            hiddview2()
        }
        binding.arrowButton3.setOnClickListener {
            hiddview3()
        }
        binding.arrowButton4.setOnClickListener {
            hiddview4()
        }
        binding.arrowButton5.setOnClickListener {
            hiddview5()
        }
        binding.arrowButton6.setOnClickListener {
            hiddview6()
        }
        binding.arrowButton7.setOnClickListener {
            hiddview7()
        }
        binding.arrowButton8.setOnClickListener {
            hiddview8()
        }
        binding.arrowButton9.setOnClickListener {
            hiddview9()
        }

        Log.d("TAG", "onCreatesdasd: " + spannableString)


    }

    private fun hiddview9() {
        if (binding.hiddenView9.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview9, AutoTransition())
            binding.hiddenView9.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton9)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview9, AutoTransition())
            binding.hiddenView9.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton9)

        }
    }

    private fun hiddview8() {
        if (binding.hiddenView8.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview8, AutoTransition())
            binding.hiddenView8.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton8)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview8, AutoTransition())
            binding.hiddenView8.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton8)

        }
    }

    private fun hiddview7() {
        if (binding.hiddenView7.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview7, AutoTransition())
            binding.hiddenView7.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton7)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview7, AutoTransition())
            binding.hiddenView7.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton7)

        }
    }

    private fun hiddview6() {
        if (binding.hiddenView6.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview6, AutoTransition())
            binding.hiddenView6.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton6)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview6, AutoTransition())
            binding.hiddenView6.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton6)

        }
    }

    private fun hiddview5() {
        if (binding.hiddenView5.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview5, AutoTransition())
            binding.hiddenView5.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton5)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview5, AutoTransition())
            binding.hiddenView5.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton5)

        }
    }


    private fun hiddview4() {
        if (binding.hiddenView4.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview4, AutoTransition())
            binding.hiddenView4.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton4)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview4, AutoTransition())
            binding.hiddenView4.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton4)

        }
    }

    private fun hiddview3() {
        if (binding.hiddenView3.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview3, AutoTransition())
            binding.hiddenView3.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton3)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview3, AutoTransition())
            binding.hiddenView3.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton3)

        }
    }

    private fun hiddview2() {
        if (binding.hiddenView2.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview2, AutoTransition())
            binding.hiddenView2.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton2)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview2, AutoTransition())
            binding.hiddenView2.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton2)

        }
    }

    fun hiddview() {
        if (binding.hiddenView.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
            binding.hiddenView.visibility = View.GONE
            Glide.with(this@FaqActivity).load(R.mipmap.bottomerrow)
                .into(binding.arrowButton)


        } else {
            TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
            binding.hiddenView.visibility = View.VISIBLE
            Glide.with(this@FaqActivity).load(R.mipmap.toperrow)
                .into(binding.arrowButton)

        }
    }

    private fun Listner() {
        lifecycleScope.launch {
            viewModel.pagesConversion.collect {
                when (it) {
                    PagesViewModel.PagesResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is PagesViewModel.PagesResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                    }

                    is PagesViewModel.PagesResponseEvent.Loading -> {
                        showProgressBar()

                    }

                    is PagesViewModel.PagesResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {

                            for (i in it.result.data!!) {
                                if (i.page_type == "privacy_policy" && type == "pricy") {
                                    binding.txtCondition.text =
                                        HtmlCompat.fromHtml(
                                            i.description.toString(),
                                            HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )

                                    /* val spannableString = SpannableString(binding.txtCondition.text)
                                      val startIndex =
                                          binding.txtCondition.text?.indexOf("support@foreverinLove.com")
                                      endIndex = startIndex?.plus("support@foreverinLove.com".length)

                                      spannableString.setSpan(
                                          clickableSpan,
                                          0,
                                          endIndex ?: 0,
                                          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                      )

                                      binding.txtCondition.text = spannableString
                                      binding.txtCondition.movementMethod =   LinkMovementMethod.getInstance()

 */


                                }
                                if (i.page_type == "terms_and_conditions" && type == "term") {
                                    binding.txtCondition.text =
                                        HtmlCompat.fromHtml(i.description.toString(), 0)
                                    /* val spannableString = SpannableString(binding.txtCondition.text)
                                     val startIndex =
                                         binding.txtCondition.text?.indexOf("support@foreverinLove.com")
                                     endIndex = startIndex?.plus("support@foreverinLove.com".length)

                                     spannableString.setSpan(
                                         clickableSpan,
                                         0,
                                         endIndex ?: 0,
                                         Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                     )

                                     binding.txtCondition.text = spannableString
                                     binding.txtCondition.movementMethod =
                                         LinkMovementMethod.getInstance()*/
                                }
                            }


                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }


        }
    }

    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            Log.d("TAG", "onClick: " + "clicked")
            // Handle the click event here for "support@foreverinLove.com"
        }
    }


}