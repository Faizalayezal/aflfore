package com.foreverinlove.network.response

data class AddReviewResponse(
    val status: Int?,
    val message: String?,
    val data: AddReviewData?
)

data class AddReviewData(

    val review_latter_profile_id: Int?,
    val review_by: Int?,
    val review_to: Int?,
    val updated_at: String?,
    val created_at: String?,

    ) : java.io.Serializable


data class onFidoData(
    val status: Int?,
    val message: String?,
    val sdk_token: String?
) : java.io.Serializable
