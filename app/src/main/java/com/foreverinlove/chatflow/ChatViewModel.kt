package com.foreverinlove.chatflow

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foreverinlove.Constant.generateChatNode
import com.foreverinlove.chatmodual.FormatDateUseCase.getDifferenceOfYears
import com.foreverinlove.chatmodual.FormatDateUseCase.toDateString
import com.foreverinlove.chatmodual.FormatDateUseCase.toTimeString
import com.foreverinlove.chatmodual.GetFileNameUseCase
import com.foreverinlove.chatmodual.NotificationHelper
import com.foreverinlove.chatmodual.OnlineStatusObj
import com.foreverinlove.firebaseChat.MessageObject
import com.foreverinlove.firebaseChat.MessageType
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.objects.*
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

private const val TAG = "ChatViewModel"

/*Bhai  pure logic class chhe dhiyan rakhjo*/
/*Make changes carefully*/
@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application, val repository: MainRepository
) : BaseViewModel(application) {

    private val messageNode = "chatMessages"
    private lateinit var database: DatabaseReference
    private lateinit var storageDatabase: StorageReference
    var tempUserDataObject: TempUserDataObject? = null

    private var currentUserId = 5
    private var otherUserId = 24
    private var nodeId = ""
    private var matchId = ""
    private val DELETE_ME = "@delMe"
    private val DELETE_ALL = "@delAll"
    private val DELETE_OTHER = "@delOther"
    val DELETED_MSG_TEXT = "This message have been deleted."
    var onSessionExpired: (() -> Unit)? = null

    private var secretKeySpec:SecretKeySpec?=null
    private val encryptionKey = byteArrayOf(9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)

    var readconverion = true


    fun start(
        currentUserId: Int,
        otherUserId: Int,
        matchId: String,
        onSessionExpired: () -> Unit,
        type: String
    ) {
        this.currentUserId = currentUserId
        this.otherUserId = otherUserId
        this.matchId = matchId
        this.onSessionExpired = onSessionExpired


        database = FirebaseDatabase.getInstance().reference
        storageDatabase = FirebaseStorage.getInstance().reference

        nodeId = generateChatNode(currentUserId, otherUserId)



        Log.d(
            TAG,
            "starssdgt: " + nodeId + "otherUserId->>" + otherUserId + "currentUserId->>" + currentUserId
        )

        // this.currentUserId = 5
        //this.otherUserId =10

        //TOKEN LAVA  MATE
        viewModelScope.launch {
            context.dataStoreGetUserData().catch {
                it.printStackTrace()
            }.collect {
                tempUserDataObject = it

                if (readconverion) readmsg(matchId, type)
                Log.d(TAG, "stasadasdsart: " + readmsg(matchId, type))
                readconverion = false
            }
        }


    }


    fun sendMessage(message: String) {
        val encryptedMessage = encryptedMessage(message)

        /*   val messageObject = MessageObject(
               currentUserId,
               System.currentTimeMillis().toString(),
               MessageType.StringMessage().type',
               message,
               ""
           )*/
        val messageObject = MessageObject(
            currentUserId,
            System.currentTimeMillis().toString(),
            MessageType.StringMessage().type,
            encryptedMessage,
            ""
        )

        database.child(messageNode).child(nodeId).push().setValue(messageObject)

        viewModelScope.launch {

            if (privateChat) {

                when (val response =
                    repository.sendPrivateChatMessage(
                        matchId,
                        message,
                        tempUserDataObject?.token ?: ""
                    )) {
                    is Resource.Error -> {

                    }

                    is Resource.Success -> {
                        if (response.data?.status == -2) {
                            onSessionExpired?.invoke()
                        }
                    }
                }
            } else {
                when (val response =
                    repository.sendChatMessage(matchId, message, tempUserDataObject?.token ?: "")) {
                    is Resource.Error -> {

                    }

                    is Resource.Success -> {
                        if (response.data?.status == -2) {
                            onSessionExpired?.invoke()
                        }
                    }
                }
            }
        }
    }

    var msgConversion = MutableLiveData<ArrayList<ChatMessageObject>>(ArrayList())

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    //grid mate image video mate
    fun addIssuePost(issuePost: ChatMessageObject) {


        Log.d(
            TAG,
            "addIssuePost: test81>>" + issuePost.deleteStatus + ">>" + issuePost.message + ">>" + issuePost.type.intType
        )

        try {
            //date is different from other items
            if (msgConversion.value?.size == 0) {
                val newDateItem = ChatMessageObject(
                    ChatMessageListItemType.DateType(),
                    "",
                    issuePost.date,
                    "",
                    null,
                    false,
                    key = "",
                    deleteStatus = ""
                )
                msgConversion.value?.add(newDateItem)
            }
            // akhi list msg ni hoy teni uper date aave
            else if (msgConversion.value?.get(msgConversion.value?.size?.minus(1) ?: -1)?.date
                != issuePost.date
            ) {
                val newDateItem = ChatMessageObject(
                    ChatMessageListItemType.DateType(),
                    "",
                    issuePost.date,
                    "",
                    null,
                    false,
                    key = "",
                    deleteStatus = ""
                )
                msgConversion.value?.add(newDateItem)
            }
            //larger than 0
            //last msg type == SingleImage
            //last msg type == ListImage
            //last msg type == Video
            //current item type == SingleImage
            //current item type == VideoItem
            if ((msgConversion.value?.size ?: 0) > 0 &&
                (msgConversion.value?.get(msgConversion.value?.size?.minus(1) ?: -1)?.type?.intType
                        == ChatMessageListItemType.SingleImageTypeSender().intType
                        || msgConversion.value?.get(
                    msgConversion.value?.size?.minus(1) ?: -1
                )?.type?.intType
                        == ChatMessageListItemType.ImageListTypeSender().intType
                        || msgConversion.value?.get(
                    msgConversion.value?.size?.minus(1) ?: -1
                )?.type?.intType
                        == ChatMessageListItemType.VideoTypeSender().intType)
                && (issuePost.type.intType == ChatMessageListItemType.SingleImageTypeSender().intType ||
                        issuePost.type.intType == ChatMessageListItemType.VideoTypeSender().intType ||
                        issuePost.type.intType == ChatMessageListItemType.ImageListTypeSender().intType)
            ) {
                msgConversion.value?.get(msgConversion.value?.size?.minus(1) ?: -1)?.apply {
                    if (filesArray?.size == 10 ||
                        issuePost.deleteStatus == DELETE_ME ||
                        issuePost.deleteStatus == DELETE_ALL
                    ) {
                        issuePost.type = ChatMessageListItemType.StringSenderType()
                        msgConversion.value?.add(issuePost)
                    } else {
                        val diff = getDifferenceOfYears(
                            this.date,
                            this.time,
                            issuePost.date,
                            issuePost.time
                        )
                        Log.d(TAG, "addIssuePost213: "+diff)
                        val ab: Long = 1000 * 60 * 10
                        if (diff > ab) msgConversion.value?.add(issuePost)
                        else if (filesArray == null) {

                            val newList = ArrayList<FileData>()
                            newList.add(FileData(this.message, this.key, issuePost.deleteStatus))
                            newList.add(
                                FileData(
                                    issuePost.message,
                                    issuePost.key,
                                    issuePost.deleteStatus
                                )
                            )
                            filesArray = newList
                            this.type = ChatMessageListItemType.ImageListTypeSender()
                            this.key = ""
                        } else {
                            filesArray!!.add(
                                FileData(
                                    issuePost.message,
                                    issuePost.key,
                                    issuePost.deleteStatus
                                )
                            )
                            this.key = ""
                            this.type = ChatMessageListItemType.ImageListTypeSender()
                            Log.d("TAG", "addIssdfsuePost: "+filesArray)


                        }
                    }
                }
                //  msgConversion.value?.add(issuePost)
            } else if (
                (msgConversion.value?.size ?: 0) > 0 &&
                (msgConversion.value?.get(msgConversion.value?.size?.minus(1) ?: -1)?.type?.intType
                        == ChatMessageListItemType.SingleImageTypeReceiver().intType
                        || msgConversion.value?.get(
                    msgConversion.value?.size?.minus(1) ?: -1
                )?.type?.intType
                        == ChatMessageListItemType.ImageListTypeReceiver().intType
                        || msgConversion.value?.get(
                    msgConversion.value?.size?.minus(1) ?: -1
                )?.type?.intType
                        == ChatMessageListItemType.VideoTypeReceiver().intType)
                && (issuePost.type.intType == ChatMessageListItemType.SingleImageTypeReceiver().intType ||
                        issuePost.type.intType == ChatMessageListItemType.VideoTypeReceiver().intType ||
                        issuePost.type.intType == ChatMessageListItemType.ImageListTypeReceiver().intType)
            ) {
                //msgConversion.value?.add(issuePost)
                msgConversion.value?.get(msgConversion.value?.size?.minus(1) ?: -1)?.apply {
                    if (filesArray?.size == 10) {
                        issuePost.type = ChatMessageListItemType.StringSenderType()
                        msgConversion.value?.add(issuePost)

                    } else {
                        val diff = getDifferenceOfYears(
                            this.date,
                            this.time,
                            issuePost.date,
                            issuePost.time
                        )

                        val ab: Long = 1000 * 60 * 10
                        if (diff > ab) msgConversion.value?.add(issuePost)
                        else if (filesArray == null) {

                            val newList = ArrayList<FileData>()
                            newList.add(FileData(this.message, this.key, issuePost.deleteStatus))
                            newList.add(
                                FileData(
                                    issuePost.message,
                                    issuePost.key,
                                    issuePost.deleteStatus
                                )
                            )
                            filesArray = newList
                            this.type = ChatMessageListItemType.ImageListTypeReceiver()
                            this.key = ""
                        } else {
                            filesArray!!.add(
                                FileData(
                                    issuePost.message,
                                    issuePost.key,
                                    issuePost.deleteStatus
                                )
                            )
                            this.type = ChatMessageListItemType.ImageListTypeReceiver()
                            this.key = ""
                        }
                    }

                }
            } else {

                if ((issuePost.type.intType == ChatMessageListItemType.SingleImageTypeSender().intType ||
                            issuePost.type.intType == ChatMessageListItemType.VideoTypeSender().intType ||
                            issuePost.type.intType == ChatMessageListItemType.FileTypeSender().intType) &&
                    (issuePost.deleteStatus == DELETE_ALL || issuePost.deleteStatus == DELETE_ME)
                ) {
                    issuePost.type = ChatMessageListItemType.StringSenderType()
                } else if ((issuePost.type.intType == ChatMessageListItemType.SingleImageTypeReceiver().intType ||
                            issuePost.type.intType == ChatMessageListItemType.VideoTypeReceiver().intType ||
                            issuePost.type.intType == ChatMessageListItemType.FileTypeReceiver().intType) &&
                    (issuePost.deleteStatus == DELETE_ALL || issuePost.deleteStatus == DELETE_OTHER)
                ) {
                    issuePost.type = ChatMessageListItemType.StringReceiverType()
                }
                Log.d(TAG, "addIssuePostsddf: "+issuePost)
                msgConversion.value?.add(issuePost)

            }

            viewModelScope.launch(Dispatchers.Main) {
                msgConversion.notifyObserver()
            }

        } catch (e: Exception) {
        }
    }

    fun getMessageList() = viewModelScope.launch(Dispatchers.IO) {
        val queryCategory: Query =
            FirebaseDatabase.getInstance().reference.child(messageNode).child(nodeId)

        Log.d("msgnodr", "getMessageList: " + messageNode + nodeId)

        queryCategory.keepSynced(true)
        //local ma catch bne

        queryCategory.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val encryptedMessage = snapshot.child("encryptedMessage").getValue(String::class.java)
                val decryptedMessage = decryptMessage(encryptedMessage?:"")
                Log.d("msgnodr", "getMessageList:40000 " + decryptedMessage)

                try {

                    snapshot.getValue(MessageObject::class.java)?.let { data ->
                        var tempItemType: ChatMessageListItemType? = null

                        if ((data.sendBy ?: 0) == currentUserId) {
                            when (data.msgType) {
                                MessageType.StringMessage().type -> tempItemType =
                                    ChatMessageListItemType.StringSenderType()

                                MessageType.ImageMessage().type -> tempItemType =
                                    ChatMessageListItemType.SingleImageTypeSender()

                                MessageType.VideoMessage().type -> tempItemType =
                                    ChatMessageListItemType.VideoTypeSender()
                            }
                        } else {
                            when (data.msgType) {

                                MessageType.StringMessage().type -> tempItemType =
                                    ChatMessageListItemType.StringReceiverType()

                                MessageType.ImageMessage().type -> tempItemType =
                                    ChatMessageListItemType.SingleImageTypeReceiver()

                                MessageType.VideoMessage().type -> tempItemType =
                                    ChatMessageListItemType.VideoTypeReceiver()
                            }
                        }

                        if (tempItemType != null) {

                            val messg = data.message ?: ""
                            Log.d("TAG", "onChildAddasaasded:4353 " + messg)

                            /* if (data.msgType == MessageType.FileMessage().type) {
                                 val pathStr =
                                     Constant.getFilePath(Constant.getFileName(msg11),filePath)
                                 if (File(pathStr).exists()) {
                                     msg11 = pathStr
                                 }
                             }*/
                            val decryptedMessage = decryptMessage(messg?:"")
                            Log.d("TAG", "onChildAddasaasded:4454 " + decryptedMessage)


                            addIssuePost(
                                ChatMessageObject(
                                    tempItemType,
                                    message = decryptedMessage,
                                    date = data.msgTimestamp?.toLong()?.toDateString() ?: "",
                                    time = data.msgTimestamp?.toLong()?.toTimeString() ?: "",
                                    key = snapshot.key.toString(),
                                    deleteStatus = data.deleteStatus ?: ""
                                )
                            )
                        }


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                var isNewData = false

                msgConversion.value?.apply {

                    for (i in indices) {

                        try {
                            if (this[i].key == snapshot.key) {

                                snapshot.getValue(MessageObject::class.java)?.let {
                                    this[i].deleteStatus = it.deleteStatus ?: ""

                                    if (
                                        this[i].type.intType == ChatMessageListItemType.VideoTypeSender().intType ||
                                        this[i].type.intType == ChatMessageListItemType.SingleImageTypeSender().intType ||
                                        this[i].type.intType == ChatMessageListItemType.ImageListTypeSender().intType
                                    ) {
                                        this[i].type = ChatMessageListItemType.StringSenderType()
                                    } else if (
                                        this[i].type.intType == ChatMessageListItemType.VideoTypeReceiver().intType ||
                                        this[i].type.intType == ChatMessageListItemType.SingleImageTypeReceiver().intType ||
                                        this[i].type.intType == ChatMessageListItemType.ImageListTypeReceiver().intType
                                    ) {
                                        this[i].type = ChatMessageListItemType.StringReceiverType()
                                    }

                                    msgConversion.value?.set(i, this[i])

                                    isNewData = true

                                }

                            } else {

                                this[i].filesArray?.let {

                                    for (z in it.indices) {
                                        if (it[z].fileKey == snapshot.key) {

                                            var oldList: List<FileData>?
                                            var newList: List<FileData>?

                                            val newSize = it.size - z - 1
                                            val tempPos: Int = i
                                            oldList = it.take(z)
                                            newList = it.takeLast(newSize)

                                            if (tempPos != -1) {
                                                val list11 = ArrayList<FileData>()
                                                val list22 = ArrayList<FileData>()

                                                oldList.let { thi -> list11.addAll(thi) }
                                                newList.let { thi -> list22.addAll(thi) }

                                                this[i].filesArray?.let {

                                                    this[i].filesArray!!.removeIf { filedata ->
                                                        filedata.fileKey != snapshot.key
                                                    }

                                                    if (this[i].type.intType == ChatMessageListItemType.VideoTypeReceiver().intType ||
                                                        this[i].type.intType == ChatMessageListItemType.SingleImageTypeReceiver().intType ||
                                                        this[i].type.intType == ChatMessageListItemType.ImageListTypeReceiver().intType
                                                    ) {
                                                        this[i].type =
                                                            ChatMessageListItemType.StringReceiverType()
                                                    } else if (this[i].type.intType == ChatMessageListItemType.VideoTypeSender().intType ||
                                                        this[i].type.intType == ChatMessageListItemType.SingleImageTypeSender().intType ||
                                                        this[i].type.intType == ChatMessageListItemType.ImageListTypeSender().intType
                                                    ) {
                                                        this[i].type =
                                                            ChatMessageListItemType.StringSenderType()
                                                    }

                                                }
                                                if (list22.isNotEmpty()) {
                                                    if (list22.size == 1) {

                                                        val newType =
                                                            if (this[i].type.intType == ChatMessageListItemType.ImageListTypeReceiver().intType) {
                                                                if (list22[0].fileString.contains("videos")) {
                                                                    ChatMessageListItemType.VideoTypeReceiver()
                                                                } else ChatMessageListItemType.SingleImageTypeReceiver()
                                                            } else if (this[i].type.intType == ChatMessageListItemType.ImageListTypeSender().intType) {
                                                                if (list22[0].fileString.contains("videos")) {
                                                                    ChatMessageListItemType.VideoTypeSender()
                                                                } else ChatMessageListItemType.SingleImageTypeSender()
                                                            } else null

                                                        newType?.let {
                                                            this.add(
                                                                tempPos + 1, ChatMessageObject(
                                                                    type = newType,
                                                                    message = list22[0].fileString,
                                                                    date = this[i].date,
                                                                    time = this[i].time,
                                                                    filesArray = null,
                                                                    isSelected = false,
                                                                    fileDownloadStatus = FileDownloadStatus.NotDownloaded,
                                                                    key = list22[0].fileKey,
                                                                    deleteStatus = list22[0].deleteStatus,
                                                                )
                                                            )
                                                        }

                                                    } else {
                                                        this.add(
                                                            tempPos + 1, ChatMessageObject(
                                                                type = this[i].type,
                                                                message = this[i].message,
                                                                date = this[i].date,
                                                                time = this[i].time,
                                                                filesArray = list22,
                                                                isSelected = this[i].isSelected,
                                                                fileDownloadStatus = this[i].fileDownloadStatus,
                                                                key = "nomeaning",
                                                                deleteStatus = this[i].deleteStatus,
                                                            )
                                                        )
                                                    }
                                                }
                                                if (list11.isNotEmpty()) {
                                                    if (list11.size == 1) {

                                                        val newType =
                                                            if (this[i].type.intType == ChatMessageListItemType.ImageListTypeReceiver().intType) {
                                                                if (list11[0].fileString.contains("videos")) {
                                                                    ChatMessageListItemType.VideoTypeReceiver()
                                                                } else ChatMessageListItemType.SingleImageTypeReceiver()
                                                            } else if (this[i].type.intType == ChatMessageListItemType.ImageListTypeSender().intType) {
                                                                if (list11[0].fileString.contains("videos")) {
                                                                    ChatMessageListItemType.VideoTypeSender()
                                                                } else ChatMessageListItemType.SingleImageTypeSender()
                                                            } else null

                                                        newType?.let {
                                                            this.add(
                                                                tempPos, ChatMessageObject(
                                                                    type = newType,
                                                                    message = list11[0].fileString,
                                                                    date = this[i].date,
                                                                    time = this[i].time,
                                                                    filesArray = null,
                                                                    isSelected = false,
                                                                    fileDownloadStatus = FileDownloadStatus.NotDownloaded,
                                                                    key = list11[0].fileKey,
                                                                    deleteStatus = list11[0].deleteStatus,
                                                                )
                                                            )
                                                        }

                                                    } else {
                                                        this.add(
                                                            tempPos, ChatMessageObject(
                                                                type = this[i].type,
                                                                message = this[i].message,
                                                                date = this[i].date,
                                                                time = this[i].time,
                                                                filesArray = list11,
                                                                isSelected = this[i].isSelected,
                                                                fileDownloadStatus = this[i].fileDownloadStatus,
                                                                key = "nomeaning",
                                                                deleteStatus = this[i].deleteStatus,
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            isNewData = true
                        }

                        if (isNewData) break

                    }

                }

                if (isNewData) {
                    viewModelScope.launch(Dispatchers.Main) {
                        msgConversion.notifyObserver()
                    }
                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private var uploadFileStatusList = ArrayList<UploadFileProgress>()

    data class UploadFileProgress(
        var data: UploadStatus,
        var key: Int,
    )

    sealed class UploadStatus {
        object Failed : UploadStatus()
        object Successful : UploadStatus()
        class InProgress(var progress: Int) : UploadStatus()
    }


    enum class SendFileType {
        Images, Videos
        //Files
    }

    fun sendFiles(bitmaps: ArrayList<Uri>, sendFileType: SendFileType) {
        uploadFileStatusList.clear()
        var selectedType: String? = null
        val typeName =
            when (sendFileType) {
                SendFileType.Images -> {
                    selectedType = MessageType.ImageMessage().type
                    "images"
                }

                SendFileType.Videos -> {
                    selectedType = MessageType.VideoMessage().type
                    "videos"
                }
                /*  SendFileType.Files -> {
                      selectedType = MessageType.FileMessage().type
                      "files"
                  }*/
            }

        GlobalScope.launch {
            for (i in bitmaps.indices) {
                Log.d("TAG", "sendFiles693: " + i)

                uploadFileStatusList.add(UploadFileProgress(UploadStatus.InProgress(0), i))

                var name = "$typeName${System.currentTimeMillis()}"

                val ab = async {

                    if (typeName == "files") {

                        var newFileName = GetFileNameUseCase().getName(bitmaps[i], context)

                        if (newFileName != "") {
                            val ext = newFileName.takeLast(4)
                            newFileName = newFileName.replace(ext, "")
                            newFileName = newFileName.take(15) + ext
                            name = newFileName
                        }

                    }
                }

                ab.join()
                if (privateChat) {
                    viewModelScope.launch {
                        repository.sendPrivateChatMessage(
                            matchId,
                            "media",
                            tempUserDataObject?.token ?: ""
                        )
                    }
                }else{
                    viewModelScope.launch {
                        repository.sendChatMessage(
                            matchId,
                            "media",
                            tempUserDataObject?.token ?: ""
                        )
                    }
                }

                storageDatabase.child(typeName).child(name)
                    .putFile(bitmaps[i])
                    .addOnProgressListener {
                        val percent = ((it.bytesTransferred * 100) / it.totalByteCount).toInt()
                        uploadFileStatusList[i].data = UploadStatus.InProgress(percent)

                        if (percent == 100) {


                            if (it.metadata != null) {
                                if (it.metadata!!.reference != null) {
                                    val result: Task<Uri> = it.storage.downloadUrl
                                    result.addOnSuccessListener { uri ->
                                        val imageUrl = uri.toString()

                                        val encryptedMessage = encryptedMessage(imageUrl)

                                        /* val messageObject = MessageObject(
                                             currentUserId,
                                             System.currentTimeMillis().toString(),
                                             selectedType,
                                             imageUrl,
                                             ""
                                         )*/
                                        val messageObject = MessageObject(
                                            currentUserId,
                                            System.currentTimeMillis().toString(),
                                            selectedType,
                                            encryptedMessage,
                                            ""
                                        )



                                        database.child(messageNode).child(nodeId).push()
                                            .setValue(messageObject)

                                        Log.d(TAG, "seddddndFiles: " + imageUrl)
                                        if (java.io.File(imageUrl).exists()) {
                                            java.io.File(imageUrl).delete()
                                        }


                                    }
                                }
                            }

                            uploadFileStatusList[i].data = UploadStatus.Successful
                        }
                        NotificationHelper(uploadFileStatusList[i], name, context)
                    }
            }
        }

    }


    var status = OnlineStatusObj("", "")

    fun getLastSeenDetails(userId: String, listener: OnOnlineStatusListener) {
        status = OnlineStatusObj("", "")

        val queryCategory: Query =
            FirebaseDatabase.getInstance().reference.child("onlineStatus").child(userId)
        queryCategory.keepSynced(true)
        //local ma catch bne
        queryCategory.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildAdded: >>$snapshot")

                try {
                    snapshot.getValue(OnlineStatusObj::class.java)?.let {
                        status.lastSeen = it.lastSeen
                        status.status = it.status
                    }
                    listener.onStatusChanged(status)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    interface OnOnlineStatusListener {
        fun onStatusChanged(data: OnlineStatusObj)
    }

    private var tempDataObject: TempUserDataObject? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStoreGetUserData().catch { it.printStackTrace() }
                .collect {
                    tempDataObject = it

                    // Log.d("TAG", "testUserGender>>11>>" + it.gender)
                }
        }
    }

    sealed class ReadMsg {
        class Success(val result: String) : ReadMsg()
        class Failure(val errorText: String) : ReadMsg()
        object Loading : ReadMsg()
        object Empty : ReadMsg()
    }


    private val _readConversion = MutableStateFlow<ReadMsg>(
        ReadMsg.Empty
    )

    fun readmsg(matchid: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _readConversion.value = ReadMsg.Loading
            when (val call = repository.ReadMessage(tempDataObject?.token ?: "", matchid, type)) {
                is Resource.Error -> {
                    _readConversion.value = ReadMsg.Failure(call.message ?: "")
                }

                is Resource.Success -> {
                    if (call.data == null) {
                        _readConversion.value = ReadMsg.Failure(call.message ?: "")
                    } else {
                        _readConversion.value = ReadMsg.Success(call.data)
                    }
                }
            }
        }
    }

    var privateChat = false

    fun chatFlow(privateChat: Boolean) {
        this.privateChat = privateChat

    }



    private fun encryptedMessage(message: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        secretKeySpec= SecretKeySpec(encryptionKey,"AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }

    private fun decryptMessage(encryptedMessage: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        secretKeySpec= SecretKeySpec(encryptionKey,"AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedBytes = cipher.doFinal(android.util.Base64.decode(encryptedMessage, android.util.Base64.DEFAULT))
        return String(decryptedBytes)
    }


}
