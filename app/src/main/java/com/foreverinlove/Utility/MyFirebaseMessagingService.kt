package com.foreverinlove.utility

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.foreverinlove.R
import com.foreverinlove.network.repository.DefaultMainRepository
import com.foreverinlove.screen.activity.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var repository: DefaultMainRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        /*CoroutineScope(Dispatchers.IO).launch {
            applicationContext.dataStoreGetUserData().firstOrNull {

                if(it.first_name != "" && it.token!="") {
                    repository.updateToken(it.token,token)
                }

                true
            }
        }*/
    }


    //apda phone ma msg aayvo
    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        Log.d(
            "TAG",
            "onMessageReceived: -->intentdata" + remoteMessage.data + "body->" + remoteMessage.notification?.body + "-->123" + remoteMessage.toString()
        )
        buildNotification(remoteMessage)


    }

    fun getLauncherClassName(context: Context): String? {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packageName = context.packageName
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            val activityInfo = resolveInfo.activityInfo
            if (activityInfo.packageName == packageName) {
                return activityInfo.name
            }
        }
        throw RuntimeException("Unable to get the launcher activity name")
    }


    private val notificationCancelTime = 30
    private fun buildNotification(remoteMessage: RemoteMessage) {
        val dataReal = (remoteMessage.data)["custom"]

        Log.d("TAG", "buildNotification: testNotificationData>>${remoteMessage.data}")

        val notificationChannelId = "1"
        val mBuilder = NotificationCompat.Builder(this)
        //mBuilder.setNumber(10)
        //clicke notification pr kri etle splash activity ma aave
        val resultIntent = Intent(applicationContext, SplashActivity::class.java)
        resultIntent.putExtra("custom", dataReal.toString())
        resultIntent.action = System.currentTimeMillis().toString()
        resultIntent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES)
        resultIntent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK


        val intent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
        }


        val title = remoteMessage.notification!!.title
        val message = remoteMessage.notification!!.body

        mBuilder.setSmallIcon(R.mipmap.bird)
        mBuilder.setContentTitle(title)
        mBuilder.setContentText(message)
        // mBuilder.setNumber(count?:0)
        val icon = BitmapFactory.decodeResource(resources, R.mipmap.bird)
        // mBuilder.setLargeIcon(icon)
        mBuilder.setSmallIcon(R.mipmap.bird)
        mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        mBuilder.setAutoCancel(true)
        mBuilder.priority = Notification.PRIORITY_HIGH
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS)
        mBuilder.setDefaults(Notification.DEFAULT_ALL)


        mBuilder.setTimeoutAfter(notificationCancelTime * 1000L)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mBuilder.setCategory(Notification.CATEGORY_CALL)
        }
        mBuilder.setContentIntent(intent)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //mNotificationManager.notify(1, mBuilder.build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mBuilder.setChannelId(notificationChannelId)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        mNotificationManager.notify(1, mBuilder.build())

    }
}