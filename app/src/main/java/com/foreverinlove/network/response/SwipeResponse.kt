package com.foreverinlove.network.response

data class SwipeResponse(
    val status:Int?,
    val message:String?,
    val data:SwipeData?
)
data class SwipeData(
    val match_status:String?,
    val like_status:String?,
    val user_id:Int?,
    val matched_user_id:String?,
    val match_id:Int?,
    val user_image_url:List<ReviewGetIMage>?,
    val match_user_image_url:List<DiscoverImage?>?,
    val match_user_name:String?,
    val remaining_likes_count:Int?,
    val remaining_profile_view_count:Int?,
    val remaining_review_later_count:Int?,
    val is_limited:String?,
    val is_order:String?,
    val is_limited_profie_view:String?,
    val is_display_my_likes:String?,
    val order:SwipeOrder?,
):java.io.Serializable
data class SwipeOrder(
    val id:Int?,
    val user_id:Int?,
    val subscription_id:Int?,
    val coins:Int?,
    val currency_code:String?,
    val start_date:String?,
    val end_date:String?,
    val payment_status:String?,
    val payment_type:String?,
    val month:String?,
    val status:String?,
    val call_chat_time_limit:Int?,
    val ar_filters:Int?,
    val is_display_my_likes:Int?,
    val profile_views_limit:Int?,
    val like_per_day:Int?,
    val private_video_call_min:Int?,
    val plan_type:String?,
    val created_at:String?,
    val updated_at:String?,
):java.io.Serializable

