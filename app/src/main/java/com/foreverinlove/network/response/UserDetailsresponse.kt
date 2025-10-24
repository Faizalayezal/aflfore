package com.foreverinlove.network.response


data class UserDetailsresponse(
    val status: Int?,
    val message: String?,
    val data: UsersDetails?,
) : java.io.Serializable

data class UsersDetails(
    var remaining_likes_count: Int?,
    var remaining_super_likes_count: Int?,
    var is_matched: String?,
    val users: UserDetailsData?,
) : java.io.Serializable

data class UserDetailsData(
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

    val user_smoking: CreateProfileResponseRelationship?,
    val user_drugs: CreateProfileResponseRelationship?,
    val user_horoscope: CreateProfileResponseRelationship?,
    val user_political_leaning: CreateProfileResponseRelationship?,
    val user_religion: CreateProfileResponseRelationship?,
    val user_first_date_ice_breaker: CreateProfileResponseRelationship?,
    val user_arts: List<CreateProfileResponseRelationship>?,
    val user_covid_vaccine: CreateProfileResponseRelationship?,
    val user_drink: CreateProfileResponseRelationship?,
    val user_dietary_lifestyle: List<CreateProfileResponseRelationship>?,
    val user_interests: List<CreateProfileResponseRelationship>?,
    val user_pets: List<CreateProfileResponseRelationship>?,
    var user_life_style: DiscoverAdditional?,
    val user_looking_for: List<CreateProfileResponseRelationship>?,
    val user_language: List<CreateProfileResponseRelationship>?,
    val user_relationship_status: CreateProfileResponseRelationship?,
    val user_educations: CreateProfileResponseRelationship?,

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
    val user_kids: List<String?>?,
    val user_hobbies: List<String?>?,
    val user_images: List<DiscoverImage?>?,
    val order_active: OrderActiveData?,
) : java.io.Serializable

data class OrderActiveData(

    val id: Int?,
    val user_id: String?,
    val subscription_id: String?,
    val currency_code: String?,
    val start_date: String?,
    val end_date: String?,
    val payment_status: String?,
    val payment_type: String?,
    val month: String?,
    val status: String?,
    val call_chat_time_limit: String?,
    val like_per_day: String?,
    val plan_type: String?,

    val created_at: String?,
    val updated_at: String?,
) : java.io.Serializable


