package com.foreverinlove.network.response

data class SettingResponse(
    val status: Int?,
    val message: String?,
    val data: SettingResponsedata?,
)

data class SettingResponsedata(
    val id: Int?,
    val user_id: Int?,
    val email: Int?,
    val distance_visible: Int?,
    val distance_unit: String?,
    val send_mail: String?,
    var show_notification: Int?,
    //  val created_at: String?,
    // val updated_at: String?,
    val api_token: String?,


    )


/*
* {"status_message":"User Unmatched successfully","status_code":"1"}
* */