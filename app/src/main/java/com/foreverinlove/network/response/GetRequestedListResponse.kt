package com.foreverinlove.network.response

data class GetRequestedListResponse(
    val status: Int?,
    val message: String?,
    val data: RequestList?
)

data class RequestList(
    val room_id: String?,
    val user_id: Int?,
    val id: Int?,
    val updated_at: String?,
    val created_at: String?,

    ) : java.io.Serializable



