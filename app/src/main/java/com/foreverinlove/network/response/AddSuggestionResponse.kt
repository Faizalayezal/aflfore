package com.foreverinlove.network.response

data class AddSuggestionResponse(
    val message: String?,
    val status: Int?,
    val data: AddSuggestionData?,
) : java.io.Serializable

data class AddSuggestionData(
    val suggestion_desc: String?,
    val updated_at: String?,
    val created_at: String?,
    val suggestion_id: Int?,
) : java.io.Serializable
