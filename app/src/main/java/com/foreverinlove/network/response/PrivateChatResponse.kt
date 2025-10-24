package com.foreverinlove.network.response

data class PrivateChatResponse(
    val status: Int?,
    val message: String?,
    val data: PrivateChatData?,

    ) : java.io.Serializable

data class PrivateChatData(
    val request_from: Int?,
    val request_to: Int?,
    val request_status: String?,
    val updated_at: String?,
    val created_at: String?,
    val user_private_chat_id: Int?,
    val match_id: String?,
    val last_message: Int?,
    val message_count: Int?,
    val user_likes_from: PrivateUserChatData?,

    ) : java.io.Serializable

data class PrivateUserChatData(
    val id: Int?,
    val like_from: String?,
    val like_to: String?,
    val match_id: String?,
    val match_status: String?,
    val like_status: String?,
    val plan_status: String?,
    val notification: String?,
    val read_status: String?,
    val matched_at: String?,

    val updated_at: String?,
    val created_at: String?,

    ) : java.io.Serializable


/*
* {"status_message":"User Unmatched successfully","status_code":"1"}
* */