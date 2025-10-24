package com.foreverinlove.utility

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.*

private const val TAG = "InternetConnectivityHel"
object InternetConnectivityHelper {

    private var privateJob : Job?=null
    private var lastStatus : Boolean? = null
    fun checkConnection(context: Context, isNewCalled:Boolean = true, onConnectivityChanged:(isConnected:Boolean)->Unit){

        if(isNewCalled) lastStatus = null

        cancelListener()

        privateJob = CoroutineScope(Dispatchers.IO).launch {
            val activeNetworkInfo =
                (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                    .activeNetworkInfo

            lastStatus = if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {

                if(lastStatus != true) onConnectivityChanged.invoke(true)

                true
            } else {

                if(lastStatus != false) onConnectivityChanged.invoke(false)

                false
            }

            delay(3000)
            checkConnection(context,false,onConnectivityChanged)
        }

    }

    fun cancelListener() {
        privateJob?.let{
            if(it.isActive) it.cancel()
        }
    }

}