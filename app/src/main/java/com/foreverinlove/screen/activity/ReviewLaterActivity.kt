package com.foreverinlove.screen.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.ReviewLaterAdepter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityReviewLaterBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.ReviewData
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.DiscoverViewModel
import com.foreverinlove.viewmodels.OpenDetailsViewModel
import com.foreverinlove.viewmodels.ReviewLatterListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewLaterActivity : BaseActivity() {
    private lateinit var binding: ActivityReviewLaterBinding
    private val viewModelReviewList: ReviewLatterListViewModel by viewModels()
    private val detailsViewModel: OpenDetailsViewModel by viewModels()

    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var reviewLaterAdepter: ReviewLaterAdepter

    private var postion: Int? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewLaterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("SavedLikeList")
        detailsViewModel.start()
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        Glide.with(this@ReviewLaterActivity).load(R.mipmap.savelike).into(binding.emptyimg)


        lifecycleScope.launch {
            viewModelReviewList.reviewdMeListConversion.collect {
                when (it) {
                    ReviewLatterListViewModel.ReviewMeResponseEvent.Empty -> Utility.hideProgressBar()
                    is ReviewLatterListViewModel.ReviewMeResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        binding.userRecy.visibility = View.GONE
                    }
                    is ReviewLatterListViewModel.ReviewMeResponseEvent.Loading -> {
                        showProgressBar()

                        binding.userRecy.visibility = View.VISIBLE
                    }
                    is ReviewLatterListViewModel.ReviewMeResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data.isNullOrEmpty()) {

                                binding.imgEmpty.visibility = View.VISIBLE
                                binding.userRecy.visibility = View.GONE
                            } else  {
                                binding.userRecy.visibility = View.VISIBLE
                                setListData(it.result.data)
                            }
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

    private fun setListData(data: List<ReviewData>) {
        binding.userRecy.apply {
           val upDataData=data.reversed()
            val toArray = ArrayList<ReviewData>()
            toArray.addAll(upDataData)

            reviewLaterAdepter = ReviewLaterAdepter(this@ReviewLaterActivity, multiListener, toArray)
            adapter = reviewLaterAdepter

        }
    }
    private var ides: Int? = null
    private val multiListener = object : ReviewLaterAdepter.onClick {
        override fun openDetail(data: ReviewData,position: Int) {

            detailsViewModel.callApiOpenDetails(data.get_request_from_user?.id.toString())
            postion=position
            ides = data.get_request_from_user?.id

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
                                this@ReviewLaterActivity,
                                DetailProfileScreenActivity::class.java
                            ).putExtra("btnVisibility",false)


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

    private fun swipeDetails() {
        lifecycleScope.launch {

            viewModel.discoverUserListConversion.collect {
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
            viewModel.swipeConversion.collect {
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


                        reviewLaterAdepter.Itemremove(postion?:0)



                    }
                }
            }
        }
    }



    private val startForProfileeResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                5 -> viewModel.swipeProfile(
                    "nope",
                    (ides ?: 0).toString()
                )
                6 -> viewModel.swipeProfile(
                    "like",
                    (ides ?: 0).toString()
                )

                7 -> viewModel.swipeProfile(
                    "super_like",
                    (ides ?: 0).toString()
                )
            }
        }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}