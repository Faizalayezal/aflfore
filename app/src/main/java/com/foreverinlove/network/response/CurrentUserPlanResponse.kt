package com.foreverinlove.network.response

data class CurrentUserPlanResponse(
    val data: List<CurrentUserPlanResponseData>?,
    val message: String?,
    val status: Int?,
): java.io.Serializable

data class CurrentUserPlanResponsePlan(

    val plan_type: String?,
    val who_views_me: String?,
    val description: String?,
    val created_at: String?,
    val search_filters: String?,
    val group_video_call_and_chat: String?,
    val title: String?,
    val currency_code: String?,
    val private_chat_request: String?,
    val month: Int?,
    val updated_at: String?,
    val like_per_day: Int?,
    //val price : Int?,
    val new_price: String?,
    val my_likes: String?,
    val id: Int?,
    val super_like_par_day: Int?,
): java.io.Serializable


data class CurrentFreeUserPlanResponse(
    val data: CurrentUserPlanResponseData?,
    val message: String?,
    val status: Int?,
): java.io.Serializable

data class CurrentUserPlanResponseData(

    val end_date: String?,
    val plan_type: String?,
    val payment_status: String?,
    val created_at: String?,
    val currency_code: String?,
    val call_chat_time_limit: Int?,
    val subscription_id: Int?,
    val payment_type: String?,
    val month: String?,
    val updated_at: String?,
    val user_id: Int?,
    val like_per_day: Int?,
    val id: Int?,
    val plan: CurrentUserPlanResponsePlan?,
    val start_date: String?,
    val status: String?,
): java.io.Serializable