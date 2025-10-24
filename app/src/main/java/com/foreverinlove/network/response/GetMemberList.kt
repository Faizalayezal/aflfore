package com.foreverinlove.network.response


data class GetMemberList(
    val status: Int?,
    val message: String?,
    val data: ArrayList<GetMemberListData>?,
) : java.io.Serializable

data class GetMemberListData(
    val strUid: Int?,
    val u_id: String?,
    var first_name: String?,
   // val user_id: String?,
   // val user_images: List<CreateProfileResponseUserImage?>?,
) : java.io.Serializable


