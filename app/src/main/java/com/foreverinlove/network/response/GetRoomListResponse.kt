package com.foreverinlove.network.response

data class GetRoomListResponse(
    val status: Int?,
    val message: String?,
    val data: List<RoomList>?
)

data class RoomList(

    val room_id: Int?,
    val room_name: String?,
    val room_icon: String?,
    val room_icon1: String?,
    val date_from: String?,
    val date_to: String?,
    val status: String?,
    val total_users: String?,
    val updated_at: String?,
    val created_at: String?,
    val unread_message_count: String?,
    val room_join_member: List<RoomMemberList>

) : java.io.Serializable

data class RoomMemberList(

    val id: Int?,
    val room_id: String?,
    val user_id: String?,
    val updated_at: String?,
    val created_at: String?,
    val user:ViewedMeData?,
    var isUserOnline: String?,
): java.io.Serializable



