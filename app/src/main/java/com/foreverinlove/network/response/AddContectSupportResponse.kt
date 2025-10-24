package com.foreverinlove.network.response


data class AddContectSupportResponse(
    val message: String?,
    val status: Int?,
    val data: AddContectSupportData?,
) : java.io.Serializable

data class AddContectSupportData(
    val name: String?,
    val email: String?,
    val description: String?,
    val updated_at: String?,
    val created_at: String?,
    val contact_id: Int?,
) : java.io.Serializable
