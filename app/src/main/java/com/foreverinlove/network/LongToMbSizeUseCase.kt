package com.foreverinlove.network

class LongToMbSizeUseCase {
    fun convert(bytes: Long):Int{
        val kilobyte: Long = 1024
        val megabyte = kilobyte * 1024
        //val gigabyte = megabyte * 1024
        //val terabyte = gigabyte * 1024

        return (bytes / megabyte).toInt()
    }

}