package com.foreverinlove.network.response

data class OldMessageListResponse(
    val status: Int?,
    val message: String?,
    val data: OldMessageDataList?
) : java.io.Serializable

data class OldMessageDataList(
    val conversationStartedArray: List<OldMessageData>?,
    val order: PlanDetailList?
): java.io.Serializable

data class OldMessageData(
    val user_id: Int?,
    val lastseen: String?,
    val user_name: String?,
    val sender_id: Int?,
    val user_image_url: List<OldImageData>?,
    val message: String?,
    val unread_message_count: Int?,
    val read_status: String?,
    val like_status: String?,
    val match_id: Int?,
    val created_at: String?,
    val order_active: PlanDetailList?
) : java.io.Serializable


data class OldImageData(
    val id: Int?,
    val user_id: String?,
    val url: String?,
    val order: String?,
    val created_at: String?,
    val updated_at: String?,
) : java.io.Serializable
