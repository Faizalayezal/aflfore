package com.foreverinlove.chatmodual

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.foreverinlove.R
import com.foreverinlove.chatflow.ChatViewModel


class NotificationHelper(uploadFileProgress: ChatViewModel.UploadFileProgress, name:String, context: Context) {

    init {

        val notificationID = uploadFileProgress.key

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?


        when(val state=uploadFileProgress.data){
            is ChatViewModel.UploadStatus.Failed -> {
                val notificationBuilder: Notification.Builder = Notification.Builder(context)
                notificationBuilder.setOngoing(false)
                    .setSmallIcon(R.drawable.close_white)
                    .setContentTitle("Upload $name")
                .setSound(null, null)
                .setContentText("Failed")
                    .setProgress(100, 0, false)

                val notification: Notification = notificationBuilder.build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelId = "Your_channel_id$notificationID"
                    val channel = NotificationChannel(
                        channelId,
                        "Upload $name",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationManager!!.createNotificationChannel(channel)
                    notificationBuilder.setChannelId(channelId)
                }

                notificationManager!!.notify(notificationID, notification)
            }
            is ChatViewModel.UploadStatus.InProgress -> {
                val notificationBuilder: Notification.Builder = Notification.Builder(context)
                notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.file_downloading)
                    .setContentTitle("Uploading $name")
                    .setContentText("In progress")
                    .setSound(null, null)
                    .setProgress(100, state.progress, false)

                val notification: Notification = notificationBuilder.build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelId = "Your_channel_id$notificationID"
                    val channel = NotificationChannel(
                        channelId,
                        "Upload $name",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationManager!!.createNotificationChannel(channel)
                    notificationBuilder.setChannelId(channelId)
                }

                notificationManager!!.notify(notificationID, notification)
            }
            is ChatViewModel.UploadStatus.Successful -> {
                val notificationBuilder: Notification.Builder = Notification.Builder(context)
                notificationBuilder.setOngoing(false)
                    .setSmallIcon(R.drawable.done_white)
                    .setContentTitle("Upload $name")
                    .setContentText("Successful")
                    .setSound(null, null)
                    .setProgress(100, 100, false)

                val notification: Notification = notificationBuilder.build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelId = "Your_channel_id$notificationID"
                    val channel = NotificationChannel(
                        channelId,
                        "Upload $name",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationManager!!.createNotificationChannel(channel)
                    notificationBuilder.setChannelId(channelId)
                }

                notificationManager!!.notify(notificationID, notification)
            }
        }

        val items = listOf(1, 2, 3, 4, 5)

        val total=items.looper { pos, item -> pos }
    }

    fun <T, R> Collection<T>.fold(
        initial: R,
        combine: (acc: R, nextElement: T) -> R
    ): R {
        var accumulator: R = initial
        for (element: T in this) {
            accumulator = combine(accumulator, element)
        }
        return accumulator
    }

    fun <T> Collection<T>.looper(
        Listener:(pos:Int,item:T)->Int
    ):Int{
        var abr=0

        forEachIndexed { index, t ->
            abr += Listener(index, t)
        }

        return abr
    }
}