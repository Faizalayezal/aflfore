package com.foreverinlove.network.response

data class FreePlanResponse(
    val message: String?,
    val status: Int?,
    val data: CurrentUserFreePlanResponsePlan?,
) : java.io.Serializable

data class CurrentUserFreePlanResponsePlan(

    val start_date: String?,
    val end_date: String?,
    val status: String?,
    val user_id: Int?,
    val subscription_id: Int?,
    val like_per_day: Int?,
    val plan_type: String?,
    val updated_at: String?,
    val created_at: String?,
    val id: Int?,


    ) : java.io.Serializable


