package com.foreverinlove.objects

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

// type
// 0=date
// 1=receiver
// 2=sender
// 3=invitation
// 4=file
// 5=video
// 6=image
// 7=image list
data class ChatMessageObject(
    var type: ChatMessageListItemType,
    var message: String,
    val date: String,
    val time: String,
    var filesArray: ArrayList<FileData>?=null ,
    var isSelected: Boolean = false,
    var fileDownloadStatus:FileDownloadStatus=FileDownloadStatus.NotDownloaded,
    var key :String,
    var deleteStatus :String,
    var userName : String = "",
    var userImage : String = "",
)

data class FileData(
    var fileString:String,
    var fileKey:String,
    var deleteStatus:String
) : java.io.Serializable

enum class FileDownloadStatus{
    NotDownloaded,
    Downloaded,
    Downloading
}

sealed class ChatMessageListItemType(val intType: Int) {
    class DateType : ChatMessageListItemType(0)

    class StringReceiverType : ChatMessageListItemType(1)//done
    class StringSenderType : ChatMessageListItemType(2)//done

    class InvitationType : ChatMessageListItemType(3)

    class VideoTypeReceiver : ChatMessageListItemType(9)
    class VideoTypeSender : ChatMessageListItemType(5)

    class SingleImageTypeReceiver : ChatMessageListItemType(10)
    class SingleImageTypeSender : ChatMessageListItemType(6)

    class FileTypeReceiver : ChatMessageListItemType(8)
    class FileTypeSender : ChatMessageListItemType(4)

    class ImageListTypeReceiver : ChatMessageListItemType(11) //done
    class ImageListTypeSender : ChatMessageListItemType(7)//done
}