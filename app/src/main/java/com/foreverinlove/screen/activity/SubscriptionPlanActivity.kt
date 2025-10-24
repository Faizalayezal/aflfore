package com.foreverinlove.screen.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.R
import com.foreverinlove.adapter.SubScriptionAdapter
import com.foreverinlove.adapter.SubsType
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivitySubscriptionPlanBinding
import com.foreverinlove.network.Utility.hideProgressBar
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.CurrentUserPlanResponseData
import com.foreverinlove.network.response.SubscriptionPlanItem
import com.foreverinlove.network.response.SubscriptionPlanListResponse
import com.foreverinlove.objects.SubscriptionList
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.SubscriptionPlanListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubscriptionPlanActivity : BaseActivity() {
    private lateinit var binding: ActivitySubscriptionPlanBinding
    private val viewModel: SubscriptionPlanListViewModel by viewModels()

    private var tempUserDataObject: TempUserDataObject? = null
   // private lateinit var iapConnector: IapConnector
  //  val isBillingClientConnected: MutableLiveData<Boolean> = MutableLiveData()


    private var freePlanData: SubscriptionPlanItem? = null
    private var trialPlanData: SubscriptionPlanItem? = null
    private var currentPlanData: CurrentUserPlanResponseData? = null
    private var allPlanData: SubscriptionPlanListResponse? = null
    private var countLoading = 0

    override fun onResume() {
        super.onResume()
        countLoading = 0
        updateLoadingState(0)
        viewModel.getCurrentUserPlan()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("GetFreePlanDetail")
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnfreetirl.visibility = View.GONE

        lifecycleScope.launch {
            dataStoreGetUserData().firstOrNull {
                if (!it.is_once_purchased) {
                    binding.btnfreetirl.visibility = View.VISIBLE
                } else {
                }
                tempUserDataObject = it
                true
            }
        }





        binding.btnfreetirl.setOnClickListener {
            if (currentPlanData == null) {
                //  successApplyDialog()
                viewModel.getFreePlan()
            } else showToast("Plan Already Active")
        }

        binding.btnBrowsePlan.setOnClickListener {
            if (currentPlanData == null)
                startActivity(
                    Intent(this@SubscriptionPlanActivity, SubscriptonPlan2Activity::class.java)
                    //.putExtra("allData", allPlanData)
                )
            else showToast("Plan Already Active")
        }

        lifecycleScope.launch {
            viewModel.subscriptionPlanReason.collect { it ->
                when (it) {
                    SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Empty -> {
                    }

                    is SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Failure -> {
                        updateLoadingState(1)
                    }

                    SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Loading -> {
                        showProgressBar()
                    }

                    is SubscriptionPlanListViewModel.SubscriptionPlanListEvent.Success -> {
                        updateLoadingState(1)
                        if (it.result.status == -2) {
                            handleSessionExpired()
                        } else if (it.result.status == 1) {

                            it.result.data?.find { freePlan -> freePlan.title == "Free Membership" }
                                ?.let { data ->
                                    freePlanData = data
                                }
                            it.result.data?.find { trialPlan -> trialPlan.title == "Trial" }
                                ?.let { it ->
                                    trialPlanData = it
                                }
                            allPlanData = it.result

                            setList()
                        }

                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.getUserCurrentPlanFlow.collect {
                when (it) {
                    is SubscriptionPlanListViewModel.GetUserCurrentPlan.Empty -> {
                        hideProgressBar()
                    }

                    is SubscriptionPlanListViewModel.GetUserCurrentPlan.Failure -> {
                        updateLoadingState(1)
                        showToast(it.errorText)

                        binding.subrecy.visibility = View.GONE


                    }

                    is SubscriptionPlanListViewModel.GetUserCurrentPlan.Loading -> {
                        showProgressBar()

                        binding.subrecy.visibility = View.VISIBLE
                    }

                    is SubscriptionPlanListViewModel.GetUserCurrentPlan.Success -> {

                        updateLoadingState(1)
                        if (it.result.status == 1) {
                            if (it.result.data.isNullOrEmpty()) {

                            } else {
                                currentPlanData = it.result.data.firstOrNull()

                                setList()
                            }
                        }
                        if (it.result.data != null) {
                            binding.btnfreetirl.visibility = View.GONE

                        }
                        if (it.result.data?.firstOrNull()?.plan?.title == "Pro Basic" ||
                            it.result.data?.firstOrNull()?.plan?.title == "Pro Premium" ||
                            it.result.data?.firstOrNull()?.plan?.title == "Pro Ultimate"
                        ) {
                            // binding.txtplan.text = "Premium Plan"

                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }
                    }
                }
            }
        }

        freePlanListner()

       /* isBillingClientConnected.value = false
        val subsList =
            listOf("com.monthlyosomatenew", "com.quarterlyosomatenew", "com.yearplanosomate")
*/
       /* iapConnector = IapConnector(
            context = this,
            subscriptionKeys = subsList,
            key = getString(R.string.app_Id),
            enableLogging = true
        )*/
      /*  iapConnector.addBillingClientConnectionListener(object : BillingClientConnectionListener {
            override fun onConnected(status: Boolean, billingResponseCode: Int) {
                Log.d(
                    "KSA",
                    "This is the status: $status and response code is: $billingResponseCode"
                )
                isBillingClientConnected.value = status
            }
        })
        iapConnector.addPurchaseListener(object : PurchaseServiceListener {
            override fun onPricesUpdated(iapKeyPrices: Map<String, DataWrappers.ProductDetails>) {
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
            }

            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
            }

        })
        iapConnector.addSubscriptionListener(object : SubscriptionServiceListener {
            override fun onPricesUpdated(iapKeyPrices: Map<String, DataWrappers.ProductDetails>) {

            }

            override fun onSubscriptionPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                Log.d("TAG", "onSubscriptionPurchased:154 " + purchaseInfo.sku)
                when (purchaseInfo.sku) {

                }
            }

            override fun onSubscriptionRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
            }

        })*/


    }

    private fun freePlanListner() {
        lifecycleScope.launch {
            viewModel.getFreePlanFlow.collect {
                when (it) {
                    is SubscriptionPlanListViewModel.GetFreePlan.Empty -> {
                        hideProgressBar()
                    }

                    is SubscriptionPlanListViewModel.GetFreePlan.Failure -> {
                        updateLoadingState(1)

                        binding.subrecy.visibility = View.GONE


                    }

                    is SubscriptionPlanListViewModel.GetFreePlan.Loading -> {
                        showProgressBar()

                        binding.subrecy.visibility = View.VISIBLE
                    }

                    is SubscriptionPlanListViewModel.GetFreePlan.Success -> {

                        updateLoadingState(1)

                        if (it.result.status == 1) {
                            successFreeApplyDialog()
                            if (it.result.data==null) {

                            } else {
                                currentPlanData = it.result.data

                            }
                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }
                    }
                }
            }
        }

    }

    private fun setList() {
        if (currentPlanData == null) {
            val adapterList = ArrayList<SubscriptionList>()
            freePlanData?.let {

                //(it?.ar_filters?:0).toString()
                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "Unlimited Likes & \n" +
                                    "Dislikes ",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "Super Likes Per Week",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            /*(it.super_like_par_day ?: 0).toString()*/
                            "My Likes",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )

                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "My Saved Likes",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )

                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "InApp Chat & \n" +
                                    "Messaging ",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )

                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Advanced Search Filters",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )

                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Who Has Viewed Me",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )
                //(it?.profile_views_limit?:0).toString()
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Who Likes Me",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "InApp Live Video Chat",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Virtual Social Hours/ \n" +
                                    "Group Video Calls",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Send Private Chat \n" +
                                    "Request From Group \n" +
                                    "Video Calls",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                subsctiptplan(
                    listData = adapterList,
                    SubsType.SubscriptionDesign
                )

            }
        }
        if (currentPlanData != null) {

            freePlanData?.let {
                val adapterList = ArrayList<SubscriptionList>()

                //(it?.ar_filters?:0).toString()
                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "Unlimited Likes & \n" +
                                    "Dislikes ",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "Super Likes Per Week",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            /*(it.super_like_par_day ?: 0).toString()*/
                            "My Likes",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )

                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "My Saved Likes",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        true,
                        true,
                        getCustomString(
                            "InApp Chat & \n" +
                                    "Messaging ",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )

                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Advanced Search Filters",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )

                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Who Has Viewed Me",
                            " ",
                            "",
                            R.color.black
                        )
                    )
                )


                //(it?.profile_views_limit?:0).toString()
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Who Likes Me",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "InApp Live Video Chat",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Virtual Social Hours/ \n" +
                                    "Group Video Calls",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                adapterList.add(
                    SubscriptionList(
                        false,
                        true,
                        getCustomString(
                            "Send Private Chat \n" +
                                    "Request From Group \n" +
                                    "Video Calls",
                            "",
                            "",
                            R.color.black
                        )
                    )
                )
                subsctiptplan(
                    listData = adapterList,
                    SubsType.SubscriptionDesign
                )

            }


        }
    }

    private fun updateLoadingState(int: Int) {
        countLoading += int

        if (countLoading == 2) hideProgressBar()
    }

    private fun getCustomString(
        desc1: String,
        desc2: String,
        desc3: String,
        color: Int
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        if (desc1 != "") {
            val redSpannable1 = SpannableString(desc1)
            redSpannable1.setSpan(ForegroundColorSpan(Color.BLACK), 0, desc1.length, 0)
            builder.append(redSpannable1)
        }

        if (desc2 != "") {
            val redSpannable1 = SpannableString(desc2)
            redSpannable1.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(applicationContext, color)),
                0,
                desc2.length,
                0
            )
            builder.append(redSpannable1)
        }
        if (desc3 != "") {
            val redSpannable1 = SpannableString(desc3)
            redSpannable1.setSpan(ForegroundColorSpan(Color.BLACK), 0, desc3.length, 0)
            builder.append(redSpannable1)
        }
        return builder
    }

    private fun subsctiptplan(listData: ArrayList<SubscriptionList>, type: SubsType) {
        binding.subrecy.apply {
            adapter = SubScriptionAdapter(this@SubscriptionPlanActivity, listData, type)

        }
    }


    private fun successFreeApplyDialog() {

        val dialog = Dialog(this, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogfreetireysucess)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.sucessaplaytransperent
                )
            )
        )

        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this@SubscriptionPlanActivity,
                    MainActivity::class.java
                ).putExtra("openProfile", "yes")
            )

            finish()
        }


        dialog.show()
    }
    fun clickSubscriptionPlan() {
    //    Log.d(TAG, "clickSubscriptionPlan:1206 " + producatId.value)

      /*  iapConnector.subscribe(
            this,
            if (producatId.value == null) "com.monthlyosomatenew" else producatId.value ?: ""
        )*/
    }

}