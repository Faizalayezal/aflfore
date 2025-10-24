package com.foreverinlove.utility

import android.content.Intent
import android.util.Log
import com.google.gson.Gson

private const val TAG = "NotificationFlowHandler"


//notification click mange krva mate
class NotificationFlowHandler {

    sealed class FlowTracker{
        class FirstTimeGroupVideoCall(val data: FirstNotificationGroupCallData):FlowTracker()
        class FirstTimeSingleVideoCall(val data: FirstNotificationSingleCallData):FlowTracker()
    }
    var flowTracker : FlowTracker? = null

    fun fetchDataFirstTime(intent: Intent) {

        Log.d(TAG, "fetchDataFirstTime: testRetrivedData>>" + intent.getStringExtra("custom"))

        val testModel = Gson().fromJson(
            intent.getStringExtra("custom"),
            FirstNotificationGroupCallCustom::class.java
        )
        val privatetestModel = Gson().fromJson(
            intent.getStringExtra("custom"),
            FirstNotificationSingleCallCustom::class.java
        )

        if (
            testModel?.message?.room_id != null &&
            testModel.message.channel_name != null &&
            testModel.message.room_name != null &&
            testModel.message.room_image != null &&
            testModel.message.created_at != null &&
            testModel.message.total_users != null){
            flowTracker = FlowTracker.FirstTimeGroupVideoCall(testModel.message)
        }


        if (
            privatetestModel?.message?.user_id != null &&
            privatetestModel.message.channel_name != null &&
            privatetestModel.message.user_name != null &&
            privatetestModel.message.user_image != null &&
            privatetestModel.message.created_at != null &&
            privatetestModel.message.receiver_u_id != null
        ){
            flowTracker = FlowTracker.FirstTimeSingleVideoCall(privatetestModel.message)
        }

        Log.d(TAG, "fetchDataFirstTime: testDataFinal>>" + testModel?.message?.room_id)
        Log.d(TAG, "fetchDataFirstTime: testDataFinal>>" + testModel?.message?.room_name)
        Log.d(TAG, "fetchDataFirstTime: testDataFinal>>" + testModel?.message?.total_users)
    }

    fun applyNotificationData(intent: Intent) :Intent{

        when(val item = flowTracker){
            is FlowTracker.FirstTimeGroupVideoCall -> {
                intent.putExtra("notificationData",item.data)
            }

            is FlowTracker.FirstTimeSingleVideoCall -> {
                intent.putExtra("notificationSingleData",item.data)
            }
            null -> Unit
        }

        return intent
    }

    fun fetchNormalNotificationData(intent: Intent?) {
        val itemData = intent?.getSerializableExtra("notificationData") as? FirstNotificationGroupCallData
        itemData?.let{
            flowTracker = FlowTracker.FirstTimeGroupVideoCall(it)
        }

        val SingleitemData = intent?.getSerializableExtra("notificationSingleData") as? FirstNotificationSingleCallData

        Log.d(TAG, "fetchNormalNotificationData: testFLowAb>>$SingleitemData")
        
        SingleitemData?.let{
            flowTracker = FlowTracker.FirstTimeSingleVideoCall(it)
        }


    }

}

data class FirstNotificationGroupCallCustom(
    val message: FirstNotificationGroupCallData?
)

data class FirstNotificationGroupCallData(
    val room_id: Int?,
    val channel_name: String?,
    val room_name: String?,
    val room_image: String?,
    val created_at: String?,
    val total_users: Int?,
):java.io.Serializable


private data class FirstNotificationSingleCallCustom(
    val message: FirstNotificationSingleCallData?
)

data class FirstNotificationSingleCallData(
    val channel_name: String?,
    val user_image: List<ImageSingleCallData>?,
    val user_name: String?,
    val user_id: Int?,
    val created_at: String?,
    val reaciver_token: String?,
    val receiver_u_id: String?,

    ):java.io.Serializable

data class ImageSingleCallData(
    val created_at: String?,
    val updated_at: String?,
    val user_id: Int?,
    val id: Int?,
    val url: String?,
    val order: String?,
    ):java.io.Serializable

