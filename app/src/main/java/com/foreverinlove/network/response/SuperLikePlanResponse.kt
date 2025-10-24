package com.foreverinlove.network.response

data class SuperLikePlanResponse(
    val data: List<SuperLikePlanResponseData>,
    val message: String?,
    val status: Int?,
)
data class BaseResponse(
    val message: String?,
    val status: Int?,
)

data class SuperLikePlanResponseData (
    val product_id:Int?,
    val product_name:String?,
    val price:String?,
    val qty:String?,
    val type : String?,
    val status : String?,
    val created_at : String?,
    val updated_at : String?,
)

data class SuperLikePurchaseResponse(
    val message: String?,
    val status: Int?,
    val data: SuperLikePlanResponseData
)


