package com.foreverinlove.network.response

data class PrivateChatListResponse(
    val status: Int?,
    val message: String?,
    val data: PrivateChatList?,

    ) : java.io.Serializable

data class PrivateChatList(

    val request_sent_users: List<PrivateUserSendChatDataList>?,

    val request_reacived_users: List<PrivateUserReacivedChatDataList>?,

    ) : java.io.Serializable

data class PrivateUserSendChatDataList(
    val user_private_chat_id: Int?,
    val request_from: Int?,
    val request_to: Int?,
    val request_status: String?,
    val invite_msg: String?,
    val created_at: String?,
    val updated_at: String?,
    val match_id: String?,
    val last_message: String?,
    val message_count: Int?,
    val unread_message_count: Int?,
    val sender_id: Int?,
    val get_request_to_user: RequestSentUserList?,
    val user_likes_from: userLikeFrom?,
) : java.io.Serializable


data class PrivateUserReacivedChatDataList(
    val user_private_chat_id: Int?,
    val request_from: Int?,
    val request_to: Int?,
    val request_status: String?,
    val invite_msg: String?,
    val created_at: String?,
    val updated_at: String?,
    val match_id: String?,
    val last_message: String?,
    val message_count: Int?,
    val unread_message_count: Int?,
    val sender_id: Int?,
    val get_request_from_user: RequestFromUserList?,
    val user_likes_from: userLikeFrom?,
) : java.io.Serializable

data class RequestSentUserList(
    var id: Int?,
    var first_name: String?,
    var last_name: String?,
    var dob: String?,
    var age: String?,
    var email: String?,
    var status: String?,
    var phone: String?,
    var gender: String?,
    var job_title: String?,
    var login_otp: String?,
    var google_id: String?,
    var fb_id: String?,
    var apple_id: String?,
    var login_type: String?,
    var otp_expird_time: String?,
    var address: String?,
    var latitude: String?,
    var longitude: String?,
    var profile_video: String?,
    var lastseen: String?,
    var fcm_token: String?,
    var api_token: String?,
    var device_type: String?,
    var about: String?,
    var email_verified_at: String?,
    var email_verified: String?,
    var email_verified_otp: String?,
    var coins: String?,
    var user_type: String?,
    var height: String?,
    var user_intrested_in: String?,
    var hobbies: String?,
    var created_at: String?,
    var updated_at: String?,
    var distance: String?,

    var user_educations: List<DiscoverAdditional>?,
    var user_looking_for: List<DiscoverAdditional>?,
    var user_dietary_lifestyle: List<DiscoverAdditional>?,
    var user_pets: List<DiscoverAdditional>?,
    var user_arts: List<DiscoverAdditional>?,
    var user_language: List<DiscoverAdditional>?,
    var user_interests: List<DiscoverAdditional>?,
    var user_drink: DiscoverAdditional?,
    var user_drugs: DiscoverAdditional?,
    var user_horoscope: DiscoverAdditional?,
    var user_religion: DiscoverAdditional?,
    var user_political_leaning: DiscoverAdditional?,
    var user_relationship_status: DiscoverAdditional?,
    var user_life_style: DiscoverAdditional?,
    var user_first_date_ice_breaker: DiscoverAdditional?,
    var user_covid_vaccine: DiscoverAdditional?,
    var user_smoking: DiscoverAdditional?,
    val user_images: List<RequestSentUserImage?>?,

    ) : java.io.Serializable


data class userLikeFrom(
    var id: Int?,
    var like_from: String?,
    var like_to: String?,
    var match_id: String?,
    var match_status: String?,
    var like_status: String?,
    var plan_status: String?,
    var notification: String?,
    var read_status: String?,
    var matched_at: String?,
    val created_at: String?,
    val updated_at: String?,
) : java.io.Serializable


data class RequestSentUserImage(
    var id: Int?,
    var user_id: String?,
    var url: String?,
    var order: String?,
    val created_at: String?,
    val updated_at: String?,

    ) : java.io.Serializable


data class RequestFromUserList(
    var id: Int?,
    var first_name: String?,
    var last_name: String?,
    var dob: String?,
    var age: String?,
    var email: String?,
    var status: String?,
    var phone: String?,
    var gender: String?,
    var job_title: String?,
    var login_otp: String?,
    var google_id: String?,
    var fb_id: String?,
    var apple_id: String?,
    var login_type: String?,
    var otp_expird_time: String?,
    var address: String?,
    var latitude: String?,
    var longitude: String?,
    var profile_video: String?,
    var lastseen: String?,
    var fcm_token: String?,
    var api_token: String?,
    var device_type: String?,
    var about: String?,
    var email_verified_at: String?,
    var email_verified: String?,
    var email_verified_otp: String?,
    var coins: String?,
    var user_type: String?,
    var height: String?,
    var user_intrested_in: String?,
    var hobbies: String?,
    var created_at: String?,
    var updated_at: String?,
    var distance: String?,

    var user_educations: List<DiscoverAdditional>?,
    var user_looking_for: List<DiscoverAdditional>?,
    var user_dietary_lifestyle: List<DiscoverAdditional>?,
    var user_pets: List<DiscoverAdditional>?,
    var user_arts: List<DiscoverAdditional>?,
    var user_language: List<DiscoverAdditional>?,
    var user_interests: List<DiscoverAdditional>?,

    var user_drink: DiscoverAdditional?,
    var user_drugs: DiscoverAdditional?,
    var user_horoscope: DiscoverAdditional?,
    var user_religion: DiscoverAdditional?,
    var user_political_leaning: DiscoverAdditional?,
    var user_relationship_status: DiscoverAdditional?,
    var user_life_style: DiscoverAdditional?,
    var user_first_date_ice_breaker: DiscoverAdditional?,
    var user_covid_vaccine: DiscoverAdditional?,
    var user_smoking: DiscoverAdditional?,

    val user_images: List<RequestSentUserImage>?,
) : java.io.Serializable


/*
 {"status_message":"User Unmatched successfully","status_code":"1"}
*/
