package com.foreverinlove.screen.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.R
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.databinding.ActivitySubScriptonPlanBinding
import com.foreverinlove.network.Utility.hideProgressBar
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.SubscriptonPlan2ViewModel
import com.limurse.iap.BillingClientConnectionListener
import com.limurse.iap.DataWrappers
import com.limurse.iap.IapConnector
import com.limurse.iap.PurchaseServiceListener
import com.limurse.iap.SubscriptionServiceListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SubscriptonPlan2Activity : BaseActivity() {
    private val viewModel: SubscriptonPlan2ViewModel by viewModels()
    private lateinit var binding: ActivitySubScriptonPlanBinding
    private var tempUserDataObject: TempUserDataObject? = null
    private lateinit var iapConnector: IapConnector
    val isBillingClientConnected: MutableLiveData<Boolean> = MutableLiveData()

//$ keytool -export -rfc -keystore new-forever.jks -alias key0 -file upload-new.pem
// C:\OSOMATE\upload_certificate.pem
    //keytool -list -v -keystore forever_navi.jks -alias C:\ForeverFolder\ForeverInLove11102023\bitbucket-code\forever_letest_upload_certificate.pem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubScriptonPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenOpened("SubscriptionPlanList")
        viewModel.start()
        isBillingClientConnected.value = false


        val subsList = listOf(
            "com.weeklyplan",
            "com.monthly",
            "com.3monthsplan",
            "com.6monthsplan"
        )
        val nonConsumablesList = listOf("com.lifetimeplan")

        iapConnector = IapConnector(
            context = this@SubscriptonPlan2Activity,
            subscriptionKeys = subsList,
            nonConsumableKeys = nonConsumablesList,
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
                Log.d("TAG", "onPricesUpdated:233 " + purchaseInfo)

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




        iapConnector.addSubscriptionListener(object : SubscriptionServiceListener {

            override fun onPricesUpdated(iapKeyPrices: Map<String, List<DataWrappers.ProductDetails>>) {

            }

            override fun onPurchaseFailed(
                purchaseInfo: DataWrappers.PurchaseInfo?,
                billingResponseCode: Int?
            ) {
                Log.d("TAG", "onSubscriptionPurchased:287 " + purchaseInfo?.sku)
            }

            override fun onSubscriptionPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                Log.d("TAG", "onSubscriptionPurchased:154 " + purchaseInfo.sku)
                when (purchaseInfo.sku) {
                    "com.weeklyplan" -> {
                        Log.d("TAG", "onSubscriptionPurchased: " + "month")
                        buyPlan("weekly")

                    }
                    "com.monthly" -> {
                        Log.d("TAG", "onSubscriptionPurchased: " + "month")
                        buyPlan("monthly")


                    }
                    "com.3monthsplan" -> {
                        Log.d("TAG", "onSubscriptionPurchased: " + "3month")
                        buyPlan("3month")


                    }
                    "com.6monthsplan" -> {
                        Log.d("TAG", "onSubscriptionPurchased: " + "6month")
                        buyPlan("6month")

                    }


                }
            }

            override fun onSubscriptionRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
            }

        })



        lifecycleScope.launch {
            dataStoreGetUserData().firstOrNull {
                tempUserDataObject = it
                true

            }
        }

        binding.imgBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.basicplan.setOnClickListener {
            //buyPlan("weekly")
            iapConnector.subscribe(
                this,
                "com.weeklyplan"
            )
        }
        binding.primiumplan.setOnClickListener {
           // buyPlan("monthly")

            iapConnector.subscribe(
                this,
                "com.monthly"
            )
        }
        binding.ultimate.setOnClickListener {
           // buyPlan("3month")
            iapConnector.subscribe(
                this,
                "com.3monthsplan"
            )
        }
        binding.sixmonth.setOnClickListener {
           // buyPlan("6month")
            iapConnector.subscribe(
                this,
                "com.6monthsplan"
            )
        }
        binding.lifetime.setOnClickListener {
            //buyPlan("lifetime")
            iapConnector.purchase(this, "com.lifetimeplan")

        }
    }

     fun buyPlan(type: String) {
        when (type) {
            "weekly" -> {
                showProgressBar()
                viewModel.purchaseBasicPlan("2", onResponse = ::onResponse)
            }

            "monthly" -> {
                showProgressBar()
                viewModel.purchaseBasicPlan("3", onResponse = ::onResponse)
            }

            "3month" -> {
                showProgressBar()
                viewModel.purchaseBasicPlan("4", onResponse = ::onResponse)
            }
            "6month" -> {
                showProgressBar()
                viewModel.purchaseBasicPlan("5", onResponse = ::onResponse)
            }
            "lifetime" -> {
                showProgressBar()
                viewModel.purchaseBasicPlan("6", onResponse = ::onResponse)
            }
        }
    }

    private fun onResponse(isSuccess: Boolean) {
        if (isSuccess) showToast("Plan Purchased Successfully")
        hideProgressBar()
        successFreeApplyDialog()
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
        dialog.findViewById<TextView>(R.id.textView3).text="Plan purchased successfully."

        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this@SubscriptonPlan2Activity,
                    MainActivity::class.java
                ).putExtra("openProfile", "yes")
            )

            finish()
        }

        dialog.show()
    }

}
