package com.foreverinlove.network.response

data class PagesResponse(
    val status: Int?,
    val message: String?,
    val data: List<PagesResponseData>?,

    )

data class PagesResponseData(
    val id: Int?,
    val title: String?,
    val page_type: String?,
    val description: String?,
)



/*
* {"status_message":"User Unmatched successfully","status_code":"1"}
* */