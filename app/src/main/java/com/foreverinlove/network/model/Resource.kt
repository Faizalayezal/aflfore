package com.foreverinlove.network.model


sealed class Resource<T>(val data: T?, val message: String?) {
    class Error<T>(message: String?) : Resource<T>(null, message)
    class Success<T>(data: T) : Resource<T>(data, null)
}
// sealed=sub class ko bundale k andar capt krke rakh skte he,differet type k state ko pass kr skte he
//jeneriack key word T gme chale em to

//bethebethu
