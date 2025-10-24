package com.foreverinlove.network.response


data class Notificationresponse(
    val status: Int?,
    val message: String?,
    val data: NotificationData?,
) : java.io.Serializable

data class NotificationData(
    val notifcation: List<Notifacation>?,

    ) : java.io.Serializable

data class Notifacation(
    val id: Int?,
    val user_id: String?,
    val sender_id: String?,
    val title: String?,
    val type: String?,
    val message: String?,
    val data: String?,
    val created_at: String?,
    val updated_at: String?,
    val first_name: String?,
    val user_image: List<CreateProfileResponseUserImage>?,

    ) : java.io.Serializable

