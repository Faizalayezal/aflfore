package com.foreverinlove.firebaseChat

data class MessageObject(
    val sendBy: Int?=null,
    val msgTimestamp: String?=null,
    val msgType: String?=null,
    var message: String?=null,
    val deleteStatus: String?=null

)

