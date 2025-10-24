package com.foreverinlove.screen.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.adapter.MyLikesAdepter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityMyLikesBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.LikesListDataResponse
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.OpenDetailsViewModel
import com.foreverinlove.viewmodels.WhoLikeMeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyLikesActivity : BaseActivity() {
    private var binding: ActivityMyLikesBinding? = null

    private val viewModelLike: WhoLikeMeViewModel by viewModels()
    private val DetailsViewModel: OpenDetailsViewModel by viewModels()
    private lateinit var whoLikeMeAdepter: MyLikesAdepter
    private var postion: Int? = null
    private var likesFlow: Boolean? = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLikesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModelLike.start("MyLike")
        DetailsViewModel.start()
        //screenOpened("WhoLikeMeList")
        Glide.with(this@MyLikesActivity).load(R.mipmap.whoviewme).into(binding!!.emptyimg)
        binding?.imgBack?.setOnClickListener {
            onBackPressed()
        }
        intent.apply {
            likesFlow = getBooleanExtra("likesFlow", false)
        }

        lifecycleScope.launch {
            viewModelLike.viewedMeListConversion.collect {
                when (it) {
                    WhoLikeMeViewModel.ViewedMeResponseEvent.Empty -> Utility.hideProgressBar()
                    is WhoLikeMeViewModel.ViewedMeResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        binding?.userRecy?.visibility = View.GONE
                    }

                    is WhoLikeMeViewModel.ViewedMeResponseEvent.Loading -> {
                        showProgressBar()

                        binding?.userRecy?.visibility = View.VISIBLE
                    }

                    is WhoLikeMeViewModel.ViewedMeResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            if (it.result.data.isNullOrEmpty()) {

                                binding?.userRecy?.visibility = View.GONE
                                binding?.imgEmpty?.visibility = View.VISIBLE
                            } else {
                                binding?.userRecy?.visibility = View.VISIBLE

                                setListData(it.result.data)

                            }
                        }
                        if (it.result.message == "Please Upgrade Your Account") {
                            successfulApplied()
                        }
                        if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }
        detailsOpenListner()


    }

    private var ides: Int? = null
    private val multiListener = object : MyLikesAdepter.onClick {
        override fun openDetail(data: LikesListDataResponse, position: Int) {
            DetailsViewModel.callApiOpenDetails(data.liking_user?.id.toString())

            postion = position
            ides = data.user?.id
        }

    }


    private fun setListData(data: List<LikesListDataResponse>) {
        binding?.userRecy?.apply {
            val upSuperLikeData = data.filter {
                it.like_status == "super_like"
            }.reversed()

            val upLikeData = data.filter {
                it.like_status == "like"
            }.reversed()
            val toArray = ArrayList<LikesListDataResponse>()
            Log.d("TAG", "setListData21354: " + likesFlow)
            if (likesFlow == true) {
                toArray.addAll(upLikeData)
                binding?.txtcount?.text = upLikeData.size.toString()
                binding?.txtlable?.text = "Saved Likes"

            } else {
                toArray.addAll(upSuperLikeData)
                binding?.txtcount?.text = upSuperLikeData.size.toString()
                binding?.txtlable?.text = "Saved Super Likes"

            }

            whoLikeMeAdepter = MyLikesAdepter(this@MyLikesActivity, multiListener, toArray)
            adapter = whoLikeMeAdepter


        }
    }


    private fun detailsOpenListner() {
        lifecycleScope.launch {
            DetailsViewModel.opneDetailsConversion.collect {
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
                                this@MyLikesActivity,
                                DetailProfileScreenActivity::class.java
                            ).putExtra("btnVisibility", true)


                            intent.putExtra("userDetailsdata", it.result.data)
                            startActivity(intent)


                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }


    private fun successfulApplied() {
        val dialog = Dialog(this@MyLikesActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogwholikeme)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@MyLikesActivity,
                    R.color.sucessaplaytransperent
                )
            )
        )



        Glide.with(this@MyLikesActivity).load(R.mipmap.wholikesmeempty)
            .into(dialog.findViewById<ImageView>(R.id.imageView13))

        dialog.findViewById<AppCompatButton>(R.id.btnBrowsePlan).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this@MyLikesActivity, SubscriptionPlanActivity::class.java))
        }

        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
            this@MyLikesActivity.onBackPressed()
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
        viewModelLike.start("MyLike")
    }


}