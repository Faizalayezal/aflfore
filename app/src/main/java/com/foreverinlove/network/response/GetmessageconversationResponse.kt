package com.foreverinlove.network.response

data class GetmessageconversationResponse(
    val status: Int?,
    val message: String?,
    val data: MessageConversation?
): java.io.Serializable
data class MessageConversation(
    val new_match_count:Int?,
    val conversation_not_started_array: List<MessageConversationList>,
): java.io.Serializable

data class MessageConversationList(
    val user_id: Int?,
    val user_name: String?,
    val lastseen: String?,
    val image: List<OldImageData>?,
    val read_status: String?,
    val like_status: String?,
    val match_id: Int?,
    val created_date: String?,
    var isUserOnline: String?,
    val order_active: PlanDetailList?
): java.io.Serializable


//List<OldImageData>?