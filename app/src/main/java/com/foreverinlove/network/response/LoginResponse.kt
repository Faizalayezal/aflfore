package com.foreverinlove.network.response

data class SignInResponse(
    val status:Int?,
    val message: String?,
    val data: SignInData?,

)
data class SignInData(
    val login_otp:String?,
    val token_type:String?,
    val session_id:String?,
    val token:String?,
    val userStatus:String?,
)








/*
* {"status_message":"User Unmatched successfully","status_code":"1"}
* */