package com.foreverinlove.network.response

data class ReasonListResponse (val status:Int?,val message:String?,val data:ArrayList<ReasonData>)
data class ReasonData(
    val id:Int?,
    val name:String?,
    val description:String?,
    val icon:String?,
    val type:String?,
    val status:String?,
    val created_at:String?,
    val updated_at:String?,
    val deleted_at:String?,
): java.io.Serializable