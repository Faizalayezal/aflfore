package com.foreverinlove.firebaseChat

sealed class MessageType{
    class StringMessage(val type:String="string"):MessageType()
    class VideoMessage(val type:String="video"):MessageType()
   // class FileMessage(val type:String="file"):MessageType()
    class ImageMessage(val type:String="image"):MessageType()
}