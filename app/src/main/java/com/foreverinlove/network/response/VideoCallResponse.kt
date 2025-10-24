package com.foreverinlove.network.response

data class VideoCallResponse(
    val message: String?,
    val status: Int?,
    val data: VideoCallData?,
) : java.io.Serializable

data class VideoCallData(
    val sender_user_id: Int?,
    val reaciver_user_id: String?,
    val reaciver_u_id: String?,
    val channel_name: String?,
    val reaciver_token: String?,
    val sender_token: String?,
    val sender_u_id: String?,
    val status: String?,
    val updated_at: String?,
    val created_at: String?,
    val on_going_call_id: Int?,
): java.io.Serializable
