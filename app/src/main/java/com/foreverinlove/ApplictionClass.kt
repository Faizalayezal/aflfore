package com.foreverinlove

import android.app.Application
import com.foreverinlove.network.response.ReasonListResponse
import com.foreverinlove.network.response.SubscriptionPlanListResponse
import com.google.android.gms.security.ProviderInstaller
import dagger.hilt.android.HiltAndroidApp
import javax.net.ssl.SSLContext

//bethebethu
@HiltAndroidApp
class ApplictionClass : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var listOfReason: ReasonListResponse? = null
    fun getListOfReason(): ReasonListResponse? = listOfReason
    fun setListOfReason(list: ReasonListResponse?) {
        listOfReason = list
    }

    private var listOfSubscriptionList: SubscriptionPlanListResponse? = null
    fun getListOfSubscriptionList(): SubscriptionPlanListResponse? = listOfSubscriptionList
    fun setListOfSubscriptionList(response: SubscriptionPlanListResponse?) {
        listOfSubscriptionList = response
    }


}