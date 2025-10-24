package com.foreverinlove.screen.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.WhoViewMeAdepter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityWhoViewMeBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.ViewedMeData
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.DiscoverViewModel
import com.foreverinlove.viewmodels.OpenDetailsViewModel
import com.foreverinlove.viewmodels.WhoViewedMeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WhoViewMeActivity : BaseActivity() {
    private var binding: ActivityWhoViewMeBinding? = null

    private val viewModel: WhoViewedMeViewModel by viewModels()
    private val detailsViewModel: OpenDetailsViewModel by viewModels()
    private val viewModelswipe: DiscoverViewModel by viewModels()
    private var postion: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhoViewMeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        detailsViewModel.start()
        screenOpened("ViewedMeList")
        binding?.imgBack?.setOnClickListener {
            onBackPressed()
        }
        Glide.with(this@WhoViewMeActivity).load(R.mipmap.lookatme).into(binding!!.emptyimg)


        lifecycleScope.launch {
            viewModel.viewedMeListConversion.collect {
                when (it) {
                    WhoViewedMeViewModel.ViewedMeResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is WhoViewedMeViewModel.ViewedMeResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        binding?.userRecy?.visibility = View.GONE
                    }

                    is WhoViewedMeViewModel.ViewedMeResponseEvent.Loading -> {
                        showProgressBar()

                        binding?.userRecy?.visibility = View.VISIBLE
                    }

                    is WhoViewedMeViewModel.ViewedMeResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data.isNullOrEmpty()) {
                                binding?.userRecy?.visibility = View.GONE
                                binding?.imgEmpty?.visibility = View.VISIBLE
                            } else {
                                binding?.userRecy?.visibility = View.VISIBLE
                                binding?.txtcount?.text = it.result.data.size.toString()

                                setListData(it.result.data)
                            }
                        }
                        if (it.result.message == "Please Upgrade Your Account") {
                            successfulApplied()
                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }
        detailsOpenListner()
        swipeDetails()
        swipeData()
    }

    private fun setListData(data: List<ViewedMeData>) {
        binding?.userRecy?.apply {
            val updatadata = data.sortedBy {
                it.updated_at  //first user mathe aave e mate
            }.reversed()
            adapter = WhoViewMeAdepter(this@WhoViewMeActivity, multiListener, updatadata)
        }
    }

    private var ides: Int? = null
    private val multiListener = object : WhoViewMeAdepter.onClick {
        override fun openDetail(data: ViewedMeData, position: Int) {
            detailsViewModel.callApiOpenDetails(data.id.toString())
            postion = position
            ides = data.id
        }


    }

    private fun detailsOpenListner() {
        lifecycleScope.launch {
            detailsViewModel.opneDetailsConversion.collect {
                when (it) {
                    OpenDetailsViewModel.OpenDetailsResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is OpenDetailsViewModel.OpenDetailsResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                    }

                    is OpenDetailsViewModel.OpenDetailsResponseEvent.Loading -> {
                        showProgressBar()

                    }

                    is OpenDetailsViewModel.OpenDetailsResponseEvent.Success -> {
                        Utility.hideProgressBar()



                        if (it.result.status == 1) {
                            val intent = Intent(
                                this@WhoViewMeActivity,
                                DetailProfileScreenActivity::class.java
                            ).putExtra("btnVisibility", false)

                            intent.putExtra("userDetailsdata", it.result.data)


                            startForProfileeResult.launch(intent)


                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }
    }

    private val startForProfileeResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d("TAG", "resultcode: " + it.resultCode)
            when (it.resultCode) {
                5 -> viewModelswipe.swipeProfile(
                    "nope",
                    (ides ?: 0).toString()
                )

                6 -> viewModelswipe.swipeProfile(
                    "like",
                    (ides ?: 0).toString()
                )

                7 -> viewModelswipe.swipeProfile(
                    "super_like",
                    (ides ?: 0).toString()
                )
            }
        }


    private fun swipeDetails() {
        lifecycleScope.launch {

            viewModelswipe.discoverUserListConversion.collect {
                when (it) {
                    DiscoverViewModel.DiscoverUserListEvent.Empty -> {

                    }

                    is DiscoverViewModel.DiscoverUserListEvent.Failure -> {
                        Toast.makeText(applicationContext, it.errorText, Toast.LENGTH_LONG).show()

                        Utility.hideProgressBar()
                    }

                    DiscoverViewModel.DiscoverUserListEvent.Loading -> {
                        showProgressBar()

                    }

                    is DiscoverViewModel.DiscoverUserListEvent.Success -> {
                        Utility.hideProgressBar()


                    }

                    DiscoverViewModel.DiscoverUserListEvent.LoadingNext -> Unit
                    is DiscoverViewModel.DiscoverUserListEvent.SuccessNext -> Unit
                }
            }


        }

    }

    private fun swipeData() {
        lifecycleScope.launch {
            viewModelswipe.swipeConversion.collect {
                when (it) {
                    is DiscoverViewModel.SwipeEvent.Empty -> {

                    }

                    is DiscoverViewModel.SwipeEvent.Failure -> {
                        Utility.hideProgressBar()

                        showToast(it.errorText)
                    }

                    is DiscoverViewModel.SwipeEvent.Loading -> {
                        Utility.hideProgressBar()
                    }

                    is DiscoverViewModel.SwipeEvent.Success -> {
                        Utility.hideProgressBar()

                        it.result.data?.let { data ->
                            if ((data.match_status ?: "") == "match") {
                                showToast("You And ${data.match_user_name} Liked Each Other")
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        NewMatchingAlgorithmActivity::class.java
                                    ).putExtra("matchData", data)
                                )
                            }
                        }


                    }
                }
            }
        }
    }


    private fun successfulApplied() {
        val dialog = Dialog(this@WhoViewMeActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogwhoviewme)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@WhoViewMeActivity,
                    R.color.sucessaplaytransperent
                )
            )
        )

        Glide.with(this@WhoViewMeActivity).load(R.mipmap.whoprofileempty)
            .into(dialog.findViewById<ImageView>(R.id.imageView13))

        dialog.findViewById<AppCompatButton>(R.id.btnBrowsePlan).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this@WhoViewMeActivity, SubscriptionPlanActivity::class.java))
        }



        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
            this@WhoViewMeActivity.onBackPressed()
        }

        dialog.setOnDismissListener {
            try {
                onBackPressed()
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }


        dialog.show()

    }


    override fun onResume() {
        super.onResume()
        viewModel.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null

    }
}