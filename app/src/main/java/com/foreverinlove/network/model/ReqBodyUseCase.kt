package com.foreverinlove.network.model

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

fun String.reqBody(): RequestBody {
    return RequestBody.create("text/plain".toMediaTypeOrNull(), this)
}

//bethe bethu
//multi part mate