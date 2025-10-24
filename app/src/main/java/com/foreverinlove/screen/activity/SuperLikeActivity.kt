package com.foreverinlove.screen.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.R
import com.foreverinlove.adapter.SuperLikeAdepter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivitySuperLikeBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.SuperLikePlanResponseData
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.GetSuperLikeViewModel
import com.limurse.iap.BillingClientConnectionListener
import com.limurse.iap.DataWrappers
import com.limurse.iap.IapConnector
import com.limurse.iap.PurchaseServiceListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SuperLikeActivity : BaseActivity(), SuperLikeAdepter.OnClick {
    private lateinit var binding: ActivitySuperLikeBinding
    private val viewModel: GetSuperLikeViewModel by viewModels()
    private lateinit var iapConnector: IapConnector
    val isBillingClientConnected: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("SuperLikeList")
        viewModel.start()
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.txtNoThanks.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            viewModel.superLikeListConversion.collect { apiData ->
                when (apiData) {
                    GetSuperLikeViewModel.SuperLikeListResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is GetSuperLikeViewModel.SuperLikeListResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(apiData.errorText)

                    }

                    is GetSuperLikeViewModel.SuperLikeListResponseEvent.Loading -> {

                    }

                    is GetSuperLikeViewModel.SuperLikeListResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (apiData.result.status == 1) {

                            setListData(apiData.result.data)

                        } else if (apiData.result.status == -2) {
                            handleSessionExpired()
                        }


                    }


                }
            }
        }
        superListener()
        binding.btnplan.setOnClickListener {
            if (selectedItem != null) {
                Log.d("TAG", "onCresfsdfdfate: " + selectedItem?.product_name)
                when (selectedItem?.product_name) {
                    "5 Super Likes" -> iapConnector.purchase(this, "5_superlike")
                    "25 Super Likes" -> iapConnector.purchase(this, "25_superlike")
                    "45 Super Likes" -> iapConnector.purchase(this, "45_superlike")

                }

                // iapConnector.purchase(this, "com.lifetimeplan")

                // viewModel.callApiConfirmData((selectedItem?.product_id?:0).toString())
            } else {
                showToast("Please Select A Plan To Continue")
            }
        }

        val consumablesList = listOf("5_superlike", "25_superlike", "45_superlike")

        iapConnector = IapConnector(
            context = this,
            consumableKeys = consumablesList,
            key = getString(R.string.app_Id),
            enableLogging = true,
        )



        iapConnector.addBillingClientConnectionListener(object : BillingClientConnectionListener {
            override fun onConnected(status: Boolean, billingResponseCode: Int) {
                Log.d(
                    "KSA",
                    "This is the status: $status and response code is: $billingResponseCode"
                )
                isBillingClientConnected.value = status
            }
        })


        iapConnector.addPurchaseListener(object : PurchaseServiceListener {
            override fun onPricesUpdated(iapKeyPrices: Map<String, List<DataWrappers.ProductDetails>>) {
                //plan ni list dekhade play console ma hoy a
                Log.d("TAG", "onPricesUpdated:229 " + iapKeyPrices)
            }

            override fun onProductPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                Log.d("TAG", "onPricesUpdated:127 " + purchaseInfo)
                Log.d("TAG", "onPricesUpdated:128 " + selectedItem?.product_id)

                viewModel.callApiConfirmData((selectedItem?.product_id ?: 0).toString())

            }

            override fun onProductRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                Log.d("TAG", "onPricesUpdated:238 " + purchaseInfo)

            }

            override fun onPurchaseFailed(
                purchaseInfo: DataWrappers.PurchaseInfo?,
                billingResponseCode: Int?
            ) {
                Log.d("TAG", "onPricesUpdated:238 " + purchaseInfo)

            }

        })


    }

    private fun setListData(data: List<SuperLikePlanResponseData>) {
        binding.recysuper.apply {
            adapter = SuperLikeAdepter(this@SuperLikeActivity, data, this@SuperLikeActivity)
        }
    }

    private fun superListener() {
        lifecycleScope.launch {
            viewModel.superLikeConversion.collect {
                when (it) {
                    GetSuperLikeViewModel.SupeLikePurchaseResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is GetSuperLikeViewModel.SupeLikePurchaseResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)
                    }

                    is GetSuperLikeViewModel.SupeLikePurchaseResponseEvent.Loading -> {
                        showProgressBar()
                    }

                    is GetSuperLikeViewModel.SupeLikePurchaseResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            showToast("Super Like Purchased Successfully")
                            onBackPressed()
                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }

    }


    var selectedItem: SuperLikePlanResponseData? = null
    override fun clickId(data: SuperLikePlanResponseData) {
        selectedItem = data
    }

}
