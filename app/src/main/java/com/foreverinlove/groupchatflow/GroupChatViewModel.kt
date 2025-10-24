package com.foreverinlove.groupchatflow

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foreverinlove.chatmodual.FormatDateUseCase
import com.foreverinlove.chatmodual.FormatDateUseCase.toDateString
import com.foreverinlove.chatmodual.FormatDateUseCase.toTimeString
import com.foreverinlove.chatmodual.GetFileNameUseCase
import com.foreverinlove.chatmodual.OnlineStatusObj
import com.foreverinlove.firebaseChat.MessageObject
import com.foreverinlove.firebaseChat.MessageType
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.*
import com.foreverinlove.objects.*
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject


private const val TAG = "GroupChatViewModel"

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    private val messageNode = "groupChatMessages"
    private lateinit var database: DatabaseReference
    private lateinit var storageDatabase: StorageReference


    private var currentUserId = 5
    private var roomUserId = 24
    private var nodeId = ""
    private var roomId = ""
    private val DELETE_ME = "@delMe"
    private val DELETE_ALL = "@delAll"
    private val DELETE_OTHER = "@delOther"
    val DELETED_MSG_TEXT = "This message have been deleted."
    var onSessionExpired: (() -> Unit)? = null
    var roomData: RoomList? = null
    var tempUserDataObject: TempUserDataObject? = null

    fun start(
        currentUserId: Int, roomUserId: Int, roomId: String, onSessionExpired: () -> Unit,
        roomData: RoomList?
    ) {
        this.roomData = roomData
        this.currentUserId = currentUserId
        this.roomUserId = roomUserId
        this.roomId = roomId
        this.onSessionExpired = onSessionExpired

        database = FirebaseDatabase.getInstance().reference
        storageDatabase = FirebaseStorage.getInstance().reference

        nodeId = roomId
        Log.d("TAG", "starssdgt: " + nodeId)
        // this.currentUserId = 5
        //this.otherUserId =10

        //TOKEN LAVA  MATE
        viewModelScope.launch {
            context.dataStoreGetUserData().catch {
                it.printStackTrace()
            }.collect {
                tempUserDataObject = it

                //  if(readconverion)readmsg(matchId)
                //  readconverion=false
            }
        }


    }

    fun sendMessage(message: String) {
        val messageObject = MessageObject(
            currentUserId,
            System.currentTimeMillis().toString(),
            MessageType.StringMessage().type,
            message,
            ""
        )
        database.child(messageNode).child(nodeId).push().setValue(messageObject)

        viewModelScope.launch {

            when (val response =
                repository.sendGroupChatMessage(roomId, message, tempUserDataObject?.token ?: "")) {
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

    //live data mate
    var msgConversion = MutableLiveData<ArrayList<ChatMessageObject>>(ArrayList())

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    //grid mate image video mate
    fun addIssuePost(issuePost: ChatMessageObject) {


        Log.d(
            "TAG",
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
                    deleteStatus = "",
                    userImage = issuePost.userImage,
                    userName = issuePost.userName,
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
                    deleteStatus = "",
                    userImage = issuePost.userImage,
                    userName = issuePost.userName,
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
                        val diff = FormatDateUseCase.getDifferenceOfYears(
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
                        }
                    }
                }
                //  msgConversion.value?.add(issuePost)
            }//recive code message
            else if (
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
                    if (filesArray?.size == 10 ||
                        issuePost.deleteStatus == DELETE_ALL ||
                        issuePost.deleteStatus == DELETE_OTHER
                    ) {

                        issuePost.type = ChatMessageListItemType.StringReceiverType()
                        msgConversion.value?.add(issuePost)

                    } else {
                        val diff = FormatDateUseCase.getDifferenceOfYears(
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

                try {

                    snapshot.getValue(MessageObject::class.java)?.let { data ->
                        var tempItemType: ChatMessageListItemType? = null
                        Log.d(
                            "TAG",
                            "onChildAdded879654: " + data.sendBy + "current" + currentUserId
                        )

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


                        val userObj = roomData?.room_join_member?.find {
                            it.user?.id == (data.sendBy ?: 0)
                        }

                        val userName = userObj?.user?.first_name ?: ""
                        val userImage = userObj?.user?.user_images?.firstOrNull()?.url ?: ""

                        Log.d(
                            TAG,
                            "onChildAdded: testFlow367>>${data.sendBy}>>$userName>>${data.message}>>$userImage"
                        )


                        if (tempItemType != null) {

                            val messg = data.message ?: ""
                            addIssuePost(
                                ChatMessageObject(
                                    tempItemType,
                                    message = messg,
                                    date = data.msgTimestamp?.toLong()?.toDateString() ?: "",
                                    time = data.msgTimestamp?.toLong()?.toTimeString() ?: "",
                                    key = snapshot.key.toString(),
                                    deleteStatus = data.deleteStatus ?: "",
                                    userName = userName,
                                    userImage = userImage,
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
                                                                    userImage = this[i].userImage,
                                                                    userName = this[i].userName,
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
                                                                userImage = this[i].userImage,
                                                                userName = this[i].userName,
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
                                                                    userImage = this[i].userImage,
                                                                    userName = this[i].userName,
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
                                                                userImage = this[i].userImage,
                                                                userName = this[i].userName,
                                                            )
                                                        )
                                                    }
                                                }
                                                /*if(list11.isNotEmpty()) this.add(tempPos, MainObj("Old2",list11 ))*/
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
                                        val messageObject = MessageObject(
                                            currentUserId,
                                            System.currentTimeMillis().toString(),
                                            selectedType,
                                            imageUrl,
                                            ""
                                        )
                                        database.child(messageNode).child(nodeId).push()
                                            .setValue(messageObject)
                                    }
                                }
                            }
                            uploadFileStatusList[i].data = UploadStatus.Successful
                        }
                        GroupChatNotificationHelper(uploadFileStatusList[i], name, context)
                    }
            }
        }
    }

    var status = OnlineStatusObj("", "")


    interface OnOnlineStatusListener {
        fun onStatusChanged(data: OnlineStatusObj)
    }


    fun deleteCurrantUserMessage() {

    }


    fun leavestart() {
        tempUserDataObject = null
        viewModelScope.launch {
            context.dataStoreGetUserData().collect {
                if (tempUserDataObject == null) {
                    tempUserDataObject = it

                }

            }
        }
    }

    sealed class LeaveRoomResponseEvent {
        class Success(val result: GetRequestedListResponse) : LeaveRoomResponseEvent()
        class Failure(val errorText: String) : LeaveRoomResponseEvent()
        object Loading : LeaveRoomResponseEvent()
        object Empty : LeaveRoomResponseEvent()
    }

    private val _leaveConversion = MutableStateFlow<LeaveRoomResponseEvent>(
        LeaveRoomResponseEvent.Empty
    )
    val leaveConversion: StateFlow<LeaveRoomResponseEvent> = _leaveConversion

    fun callLeaveRoomApi(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _leaveConversion.value = LeaveRoomResponseEvent.Loading
            when (val call = repository.LeaveRoom(tempUserDataObject?.token ?: "", uid)) {
                is Resource.Error -> {
                    _leaveConversion.value = LeaveRoomResponseEvent.Failure(call.message ?: "")
                }
                is Resource.Success -> {
                    if (call.data == null) {
                        _leaveConversion.value = LeaveRoomResponseEvent.Failure(call.message ?: "")
                    } else {
                        _leaveConversion.value = LeaveRoomResponseEvent.Success(call.data)
                    }
                }
            }
        }
    }


    sealed class PrivateChatResponseEvent {
        class Success(val result: PrivateChatResponse) : PrivateChatResponseEvent()
        class Failure(val errorText: String) : PrivateChatResponseEvent()
        object Loading : PrivateChatResponseEvent()
        object Empty : PrivateChatResponseEvent()
    }

    private val _privateChatConversion = MutableStateFlow<PrivateChatResponseEvent>(
        PrivateChatResponseEvent.Empty
    )
    val privateChatConversion: StateFlow<PrivateChatResponseEvent> = _privateChatConversion

    fun callPrivateChatApi(uid: String, usermsg: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _privateChatConversion.value = PrivateChatResponseEvent.Loading
            when (val call =
                repository.sendMessagePrivate(tempUserDataObject?.token ?: "", uid, usermsg)) {
                is Resource.Error -> {
                    _privateChatConversion.value =
                        PrivateChatResponseEvent.Failure(call.message ?: "")
                }
                is Resource.Success -> {
                    if (call.data == null) {
                        _privateChatConversion.value =
                            PrivateChatResponseEvent.Failure(call.message ?: "")
                    } else {
                        _privateChatConversion.value = PrivateChatResponseEvent.Success(call.data)
                    }
                }
            }
        }
    }

    // online offline green dot
    private var onlineStatusFlowJob: Job? = null
    fun getOnlineStatusOfUsers(
        conversationLists: List<RoomMemberList>,
        onDataReceived: (List<RoomMemberList>) -> Unit
    ) {
        onlineStatusFlowJob?.cancel()
        onlineStatusFlowJob = viewModelScope.launch {
            conversationLists.forEachIndexed { index, messageConversationList ->
                findStatusFromFireBase(messageConversationList.user_id.toString()) {
                    conversationLists[index].isUserOnline = it
                    Log.d("TAG", "getOnlineStatusOfUsers: " + it)
                    onDataReceived.invoke(conversationLists)
                }
            }
        }
    }

    private fun findStatusFromFireBase(otherUserId: String, onDataReceived: (String) -> Unit) {
        database.child("onlineStatus").child(otherUserId).child("status")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val result = snapshot.getValue(String::class.java)
                        result?.let {
                            onDataReceived.invoke(it)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun deleteUseDelete(SandId: Int) {
        val queryCategory: Query =
            FirebaseDatabase.getInstance().reference.child(messageNode).child(nodeId)

        Log.d(TAG, "deleteUseDeldddete: " + queryCategory)

        val listener=object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val msf = ArrayList<String?>()
                for (appleSnapshot in snapshot.children) {
                    appleSnapshot.getValue(MessageObject::class.java)?.let { data ->
                        Log.d("TAG", "onDhfhgfataChange: " + data)

                        if (data.sendBy == SandId) {

                            msf.add(appleSnapshot.key)
                            //  appleSnapshot.ref.removeValue()
                        }
                    }
                    Log.d(TAG, "onDataChsdfsdfange: " + appleSnapshot.key)

                }

                queryCategory.removeEventListener(this)
                deleteThisItem(msf)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled", error.toException());
            }

        }

        queryCategory.addListenerForSingleValueEvent(listener)

    }

    private fun deleteThisItem(msf: java.util.ArrayList<String?>) {

        for (s in msf) {

            FirebaseDatabase.getInstance().reference.child(messageNode).child(nodeId)
                .child(s.toString()).removeValue()
        }

    }


}



