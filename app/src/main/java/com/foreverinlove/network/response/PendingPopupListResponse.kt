package com.foreverinlove.network.response

data class PendingPopupListResponse(
    val status:Int?,
    val message:String?,
    val data:List<PendingPopupData>?,
)
data class PendingPopupData(
    val id:Int?,
    val name:String?,
    val description:String?,
    val icon:String?,
    val txt_color:String?,
    val bg_color:String?,
    val created_at:String?,
    val updated_at:String?,
    val screen_data:List<PendingPopupScreenData>?,
)
data class PendingPopupScreenData(
    var id:Int?,
    var popup_id:Int?,
    var screen_id:Int?,
    var created_at:String?,
    var updated_at:String?,
    var name:String?,
    var description:String?,
    var icon:String?,
    var txt_color:String?,
    var bg_color:String?,
)
