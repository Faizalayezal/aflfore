package com.foreverinlove.network.response

data class OtpResponse(
    val status: Int?,
    val message: String?,
    val data: OtpResponseData?,

    )

data class OtpResponseData(
    val token: String?,
    val token_type: String?,
    val user: OtpResponseUserData?,
)

data class OtpResponseUserData(
    val id: Int?,
    val first_name: String?,
    val last_name: String?,
    val dob: String?,
    val age: String?,
    val email: String?,
    val status: String?,
    val phone: String?,
    val gender: String?,
    val job_title: String?,
    val login_otp: String?,
    val google_id: String?,
    val fb_id: String?,
    val apple_id: String?,
    val login_type: String?,
    val otp_expird_time: String?,
    val address: String?,
    val latitude: String?,
    val longitude: String?,
    val height: String?,
    val image1: String?,
    val image2: String?,
    val image3: String?,
    val image4: String?,
    val image5: String?,
    val image6: String?,
    val about: String?,
    val profile_video: String?,
    val lastseen: String?,
    val fcm_token: String?,
    val api_token: String?,

    val device_type: String?,
    val email_verified_at: String?,
    val email_verified: String?,
    val email_verified_otp: String?,

    val user_type: String?,
    val created_at: String?,
    val updated_at: String?,
    val user_kids: List<String?>?,
    val user_hobbies: List<String?>?,
)


/*
* {"status_message":"User Unmatched successfully","status_code":"1"}
* */