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
import com.foreverinlove.adapter.WhoLikesMeAdepter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivityWhoLikesMeBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.LikesListDataResponse
import com.foreverinlove.network.response.UsersDetails
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.DiscoverViewModel
import com.foreverinlove.viewmodels.OpenDetailsViewModel
import com.foreverinlove.viewmodels.WhoLikeMeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WhoLikesMeActivity : BaseActivity() {
    private var binding: ActivityWhoLikesMeBinding? = null
    private val viewModelLike: WhoLikeMeViewModel by viewModels()
    private val DetailsViewModel: OpenDetailsViewModel by viewModels()
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var whoLikeMeAdepter: WhoLikesMeAdepter
    private var postion: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhoLikesMeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModelLike.start("WhoLike")
        DetailsViewModel.start()
        screenOpened("WhoLikeMeList")
        Glide.with(this@WhoLikesMeActivity).load(R.mipmap.whoviewme).into(binding!!.emptyimg)
        binding?.imgBack?.setOnClickListener {
            onBackPressed()
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
                                binding?.txtcount?.text = it.result.data.size.toString()
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
        swipeDetails()
        swipeData()

    }

    private var ides: Int? = null
    private val multiListener = object : WhoLikesMeAdepter.onClick {
        override fun openDetail(data: LikesListDataResponse, position: Int) {
            DetailsViewModel.callApiOpenDetails(data.user?.id.toString())

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
            toArray.addAll(upSuperLikeData)
            toArray.addAll(upLikeData)


            whoLikeMeAdepter = WhoLikesMeAdepter(this@WhoLikesMeActivity, multiListener, toArray)
            adapter = whoLikeMeAdepter


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

                        var countValue = binding?.txtcount?.text.toString()
                        countValue = countValue.toIntOrNull()?.minus(1).toString()
                        // countValue.toString()
                        binding?.txtcount?.text = countValue
                        whoLikeMeAdepter.Itemremove(postion ?: 0)


                    }
                }
            }
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
                                this@WhoLikesMeActivity,
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


    private val startForProfileeResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {it
            Log.d("TAG", "resultcode: " + it.resultCode + it.data)
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

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }


    private fun successfulApplied() {
        val dialog = Dialog(this@WhoLikesMeActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogwholikeme)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@WhoLikesMeActivity,
                    R.color.sucessaplaytransperent
                )
            )
        )



        Glide.with(this@WhoLikesMeActivity).load(R.mipmap.wholikesmeempty)
            .into(dialog.findViewById<ImageView>(R.id.imageView13))

        dialog.findViewById<AppCompatButton>(R.id.btnBrowsePlan).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this@WhoLikesMeActivity, SubscriptionPlanActivity::class.java))
        }

        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
            this@WhoLikesMeActivity.onBackPressed()
        }
        dialog.setOnDismissListener {
            try {
                onBackPressed()
            }catch (e: Exception){
                e.printStackTrace()

            }
        }


        dialog.show()

    }

    override fun onResume() {
        super.onResume()
        viewModelLike.start("WhoLike")
    }




}