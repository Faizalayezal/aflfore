package com.foreverinlove.network.response


data class ViewedMeListResponse(val status: Int?, val message: String?, val data: List<ViewedMeData>?)
data class ViewedMeData(
    val id: Int?,
    val first_name: String?,
    val last_name: String?,
    val dob: String?,
    val age: String?,
    val email: String?,
    val status: String?,
    val phone: String?,
    val gender: String?,

    val user_intrested_in: String?,

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
    val created_at: String?,
    val updated_at: String?,


    val user_images: List<ReviewGetIMage>?,

): java.io.Serializable

