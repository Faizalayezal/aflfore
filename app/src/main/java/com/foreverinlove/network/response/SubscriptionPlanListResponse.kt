package com.foreverinlove.network.response

data class SubscriptionPlanListResponse (
    val status:Int?,
    val message:String?,
    val data:ArrayList<SubscriptionPlanItem>?
):java.io.Serializable
data class SubscriptionPlanItem(
    val id:Int?,
    val title:String?,
    val description:String?,
    val search_filters:String?,
    val like_per_day:Int?,
    val super_like_par_day:String?,
    val group_video_call_and_chat:String?,
    val video_call_duration:String?,
    val my_likes:String?,
    val who_views_me:String?,
    val private_chat_request:String?,
    // val price: Int?,
    val new_price: String?,
    val currency_code:String?,
    val month:String?,
    val plan_duration:Int?,
    val plan_type:String?,
    val created_at:String?,
    val updated_at:String?,
    val is_active:Int?,
):java.io.Serializable