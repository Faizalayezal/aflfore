package com.foreverinlove.groupchatflow

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.MediaListActivity
import com.foreverinlove.chatmodual.FormatDateUseCase.getTimeAgoFromDateOnly
import com.foreverinlove.chatmodual.ImageBorderOption
import com.foreverinlove.chatmodual.ImageViewExt.loadImageWithGlide
import com.foreverinlove.chatmodual.UpdateImageGridUseCase
import com.foreverinlove.databinding.*
import com.foreverinlove.objects.ChatMessageListItemType
import com.foreverinlove.objects.ChatMessageObject
import com.foreverinlove.objects.FileData
import kotlinx.coroutines.runBlocking

private const val TAG = "ChatMessageListAdapter"
private var listFileStatus = ArrayList<MediaListActivity.FileStatus>()
private var currentPosTop = 0
private var filesList = ArrayList<MediaListActivity.MediaObject>()


class GroupChatAdepter(
    private val Listener: OnMultiSelectChangeListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list = ArrayList<ChatMessageObject>()


    fun updateData(msglist: ArrayList<ChatMessageObject>) {
        runBlocking {
            Log.d(TAG, "updateData: testflow42>>" + msglist.size + ">>" + list.size)
            //DiffUtil etle msg update krva mate gme te vstu ne update krva mate use thy

            val diffResult = DiffUtil.calculateDiff(MessageListDiffUtil(list, msglist), true)
            list.clear()
            list.addAll(msglist)

            diffResult.dispatchUpdatesTo(this@GroupChatAdepter)
            Listener.scrollToBottom()
            if (list.isNotEmpty()) notifyItemChanged(list.size - 1)
        }
    }

    //data show mate
    override fun getItemViewType(position: Int): Int {
        return try {
            list[position].type.intType
        } catch (e: Exception) {
            0
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Log.d(TAG, "onCreateViewHolder: test>>" + viewType)

        return when (viewType) {
            ChatMessageListItemType.StringSenderType().intType -> {
                val itemBinding = ItemChatMessageSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SenderHolder(itemBinding)
            }
            ChatMessageListItemType.StringReceiverType().intType -> {
                val itemBinding = ItemChatMessageReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceiverHolder(itemBinding)
            }

            ChatMessageListItemType.SingleImageTypeSender().intType -> {
                val itemBinding = ItemChatMessageSingleImageSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SingleImageSenderHolder(itemBinding)
            }
            ChatMessageListItemType.SingleImageTypeReceiver().intType -> {
                val itemBinding = ItemChatMessageSingleImageReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SingleImageReceiverHolder(itemBinding)
            }


            ChatMessageListItemType.ImageListTypeSender().intType -> {
                val itemBinding = ItemChatMessageImageListSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageListSenderHolder(itemBinding)
            }
            ChatMessageListItemType.ImageListTypeReceiver().intType -> {
                val itemBinding = ItemChatMessageImageListReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageListReceiverHolder(itemBinding)
            }


            ChatMessageListItemType.VideoTypeSender().intType -> {
                val itemBinding = ItemChatMessageVideoSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VideoSenderHolder(itemBinding)
            }
            ChatMessageListItemType.VideoTypeReceiver().intType -> {
                val itemBinding = ItemChatMessageVideoReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VideoReceiverHolder(itemBinding)
            }

            ChatMessageListItemType.InvitationType().intType -> {
                val itemBinding = ItemAcceptanceBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                InvitationHolder(itemBinding)
            }

            ChatMessageListItemType.DateType().intType -> {
                val itemBinding = ItemChatMessageDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                DateHolder(itemBinding)
            }

            else -> {
                val itemBinding =
                    ItemBlankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BlankHolder(itemBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        Log.d(
            TAG,
            "onBindViewHolder: testAllDataTest>>${list[position].userName}>>${list[position].userImage}"
        )

        when (holder.itemViewType) {

            ChatMessageListItemType.StringSenderType().intType -> {
                (holder as SenderHolder).bind(
                    position,
                    list[position],
                    //itemEventListener,
                    //  isMultiSelect
                )
            }
            ChatMessageListItemType.StringReceiverType().intType -> {
                (holder as ReceiverHolder).bind(
                    position,
                    list[position],
                    itemEventListener,
                    list[position].userImage,
                    list[position].userName,

                    // isMultiSelect
                )
            }

            ChatMessageListItemType.SingleImageTypeSender().intType -> {
                (holder as SingleImageSenderHolder).bind(
                    position,
                    list[position],
                    itemEventListener,
                    isMultiSelect
                )
            }
            ChatMessageListItemType.SingleImageTypeReceiver().intType -> {
                (holder as SingleImageReceiverHolder).bind(
                    position,
                    list[position],
                    itemEventListener,
                    isMultiSelect,
                    list[position].userImage,
                    list[position].userName,
                )
            }

            ChatMessageListItemType.ImageListTypeSender().intType -> {
                (holder as ImageListSenderHolder).bind(
                    position,
                    list[position],
                    itemEventListener,
                    isMultiSelect
                )
            }
            ChatMessageListItemType.ImageListTypeReceiver().intType -> {
                (holder as ImageListReceiverHolder).bind(
                    position,
                    list[position],
                    itemEventListener,
                    isMultiSelect,
                    list[position].userImage,
                    list[position].userName,
                )
            }

            ChatMessageListItemType.VideoTypeSender().intType -> {
                (holder as VideoSenderHolder).bind(
                    position,
                    list[position],
                    itemEventListener,
                    isMultiSelect
                )
            }
            ChatMessageListItemType.VideoTypeReceiver().intType -> {
                (holder as VideoReceiverHolder).bind(
                    position,
                    list[position],
                    itemEventListener,
                    isMultiSelect,
                    list[position].userImage,
                    list[position].userName,
                )
            }

            ChatMessageListItemType.InvitationType().intType -> {
                (holder as InvitationHolder).bind(list[position])
            }

            ChatMessageListItemType.DateType().intType -> {
                (holder as DateHolder).bind(list[position])
            }


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun changeAllSelection(boolean: Boolean) {
        for (i in list.indices) {
            list[i].isSelected = boolean
        }
        sendCountToActivity()
    }

    private var isMultiSelect = false

    fun changeMultiSelect(boolean: Boolean) {
        isMultiSelect = boolean
        notifyDataSetChanged()
    }

    private val itemEventListener = object : OnItemEventListener {
        override fun onLongPressListener(int: Int) {
            if (!isMultiSelect) {
                changeMultiSelect(true)
            } else {
                changeAllSelection(false)
                changeMultiSelect(false)
            }
            Listener.onMultiChange(isMultiSelect)
        }

        override fun onSmallPressListener(int: Int) {
            sendCountToActivity()
        }

        override fun onFilesListListener(list: ArrayList<FileData>, isSender: Boolean) {
            Listener.onFilesListOpen(list, isSender)
        }


        override fun onSingleFileClicked(pos: Int, message: String) {

            Listener.onSingleFileOpen(pos, message)


        }
    }

    private fun sendCountToActivity() {

        Log.d(TAG, "sendCountToActivity: testAbcmnd>>")

        val chatMessageObjectList = ArrayList<ChatMessageObject>()

        var count = 0
        for (i in list.indices) {
            if (list[i].isSelected &&
                (
                        list[i].type.intType == ChatMessageListItemType.StringReceiverType().intType ||
                                list[i].type.intType == ChatMessageListItemType.StringSenderType().intType ||

                                list[i].type.intType == ChatMessageListItemType.FileTypeSender().intType ||
                                list[i].type.intType == ChatMessageListItemType.FileTypeReceiver().intType ||

                                list[i].type.intType == ChatMessageListItemType.VideoTypeSender().intType ||
                                list[i].type.intType == ChatMessageListItemType.VideoTypeReceiver().intType ||

                                list[i].type.intType == ChatMessageListItemType.SingleImageTypeSender().intType ||
                                list[i].type.intType == ChatMessageListItemType.SingleImageTypeReceiver().intType ||

                                list[i].type.intType == ChatMessageListItemType.ImageListTypeSender().intType ||
                                list[i].type.intType == ChatMessageListItemType.ImageListTypeReceiver().intType
                        )
            ) {


            }
        }

        chatMessageObjectList.forEach {
            Log.d(TAG, "sendCountToActivity: test402>>" + it.message)
        }

        Listener.onSelectedCountChange(count, chatMessageObjectList)
        notifyDataSetChanged()
    }


    class MessageListDiffUtil(
        private val oldList: ArrayList<ChatMessageObject>,
        private val newList: ArrayList<ChatMessageObject>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].key === newList[newItemPosition].key
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            val oldData = oldList[oldPosition]
            val newData = newList[newPosition]

            Log.d(
                TAG,
                "areContentsTheSame: testflowTest>>" + oldData.deleteStatus + "==" + newData.deleteStatus + ">>" + oldData.message + "==" + newData.message
            )

            return (
                    oldData.key == newData.key &&
                            oldData.type.intType == newData.type.intType &&
                            oldData.message == newData.message &&
                            oldData.date == newData.date &&
                            oldData.deleteStatus == newData.deleteStatus &&
                            oldData.time == newData.time &&
                            (oldData.filesArray?.size ?: -1) == (newData.filesArray?.size ?: 0) &&
                            oldData.isSelected == newData.isSelected &&
                            oldData.fileDownloadStatus == newData.fileDownloadStatus
                    )

        }
    }


    interface OnMultiSelectChangeListener {
        fun onMultiChange(isMultiSelect: Boolean)
        fun onSelectedCountChange(count: Int, list: ArrayList<ChatMessageObject>)
        fun onFilesListOpen(list: ArrayList<FileData>, isSender: Boolean)
        fun onSingleFileOpen(pos: Int, message: String)
        fun scrollToBottom()
    }

    interface OnItemEventListener {
        fun onLongPressListener(int: Int)
        fun onSmallPressListener(int: Int)
        fun onFilesListListener(list: ArrayList<FileData>, isSender: Boolean)
        fun onSingleFileClicked(pos: Int, message: String)
    }


    //😃*************** View Holders ***************😃\\

    class SenderHolder(val binder: ItemChatMessageSenderBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(
            pos: Int,
            data: ChatMessageObject,
            //  eventListener: OnItemEventListener,
            //isMultiSelect: Boolean
        ) {
            binder.txtDate.text = data.time
            binder.txtMessage.text = data.message

            Log.d(TAG, "bind: test962>>" + data.deleteStatus + ">>" + data.message)

            /* if (data.deleteStatus == DELETE_ME || data.deleteStatus == DELETE_ALL) {
                 binder.txtMessage.text = DELETED_MSG_TEXT
             } else binder.txtMessage.text = data.message

             if (isMultiSelect && data.deleteStatus != DELETE_ME && data.deleteStatus != DELETE_ALL) {
                 binder.imgCheck.visibility = View.VISIBLE
                 binder.imgCheck.messageSelectionToggle(data.isSelected)
             } else binder.imgCheck.visibility = View.GONE

             binder.root.setOnLongClickListener {
                 eventListener.onLongPressListener(pos)
                 return@setOnLongClickListener true
             }
             binder.root.setOnClickListener {
                 if (isMultiSelect) {
                     if (data.deleteStatus == DELETE_ME || data.deleteStatus == DELETE_ALL) {
                         //click not allowed if msg is deleted
                     } else {
                         data.isSelected = !data.isSelected
                         if (data.isSelected) binder.imgCheck.messageSelectionToggle(true)
                         else binder.imgCheck.messageSelectionToggle(false)
                         eventListener.onSmallPressListener(pos)
                     }
                 }
             }*/
        }
    }

    class ReceiverHolder(
        val binder: ItemChatMessageReceiverBinding,

        ) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(
            pos: Int,
            data: ChatMessageObject,
            eventListener: OnItemEventListener,
            otherUserImage: String,
            otherUserName: String,
            //  isMultiSelect: Boolean
        ) {
            binder.txtDate.text = data.time

            binder.txtMessage.text = data.message

            Log.d(TAG, "bind: testflowRecievr>>" + data.deleteStatus + ">>" + data.message)

            binder.imgUser.loadImageWithGlide(otherUserImage, ImageBorderOption.CIRCLE)
            binder.txtUserName.text = otherUserName



            binder.root.setOnLongClickListener {
                eventListener.onLongPressListener(pos)
                return@setOnLongClickListener true
            }
            binder.root.setOnClickListener {

            }
        }
    }

    class SingleImageSenderHolder(val binder: ItemChatMessageSingleImageSenderBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(
            pos: Int, data: ChatMessageObject,
            eventListener: OnItemEventListener,
            isMultiSelect: Boolean
        ) {
            binder.txtDate.text = data.time

            Glide.with(itemView.context).load(data.message).into(binder.imgUploaded)

            binder.root.setOnClickListener {
                //  data.isSelected = !data.isSelected
                //  eventListener.onSingleFileClicked(pos, data.message)
                binder.root.setOnClickListener {
                    if (isMultiSelect) {
                        data.isSelected = !data.isSelected

                        eventListener.onSmallPressListener(pos)
                    } else {
                        eventListener.onFilesListListener(
                            arrayListOf(
                                FileData(
                                    data.message,
                                    data.key,
                                    data.deleteStatus
                                )
                            ), true
                        )
                    }
                }

            }
            binder.root.setOnLongClickListener {
                eventListener.onLongPressListener(pos)


                return@setOnLongClickListener true
            }

        }
    }

    class SingleImageReceiverHolder(
        val binder: ItemChatMessageSingleImageReceiverBinding,
    ) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(
            pos: Int, data: ChatMessageObject,
            eventListener: OnItemEventListener,
            isMultiSelect: Boolean,
            otherUserImage: String,
            otherUserName: String,

            ) {
            binder.txtDate.text = data.time

            binder.imgUser.loadImageWithGlide(otherUserImage, ImageBorderOption.CIRCLE)
            binder.imgUploaded.loadImageWithGlide(data.message)
            binder.txtUserName.text = otherUserName


            binder.root.setOnLongClickListener {
                return@setOnLongClickListener true
            }
            binder.root.setOnClickListener {
                if (isMultiSelect) {
                    data.isSelected = !data.isSelected

                    eventListener.onSmallPressListener(pos)
                } else {
                    eventListener.onFilesListListener(
                        arrayListOf(
                            FileData(
                                data.message,
                                data.key,
                                data.deleteStatus
                            )
                        ), false
                    )
                }
            }
        }
    }

    class VideoSenderHolder(val binder: ItemChatMessageVideoSenderBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(
            pos: Int, data: ChatMessageObject, eventListener: OnItemEventListener,
            isMultiSelect: Boolean
        ) {
            binder.txtDate.text = data.time
            Glide.with(itemView.context).load(data.message).into(binder.imgVideoFrame)


            binder.root.setOnLongClickListener {
                eventListener.onLongPressListener(pos)
                return@setOnLongClickListener true
            }
            binder.root.setOnClickListener {
                if (isMultiSelect) {
                    data.isSelected = !data.isSelected

                    eventListener.onSmallPressListener(pos)
                } else {
                    eventListener.onFilesListListener(
                        arrayListOf(
                            FileData(
                                data.message,
                                data.key,
                                data.deleteStatus
                            )
                        ), true
                    )
                }
            }
        }
    }

    class VideoReceiverHolder(
        val binder: ItemChatMessageVideoReceiverBinding,
    ) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(
            pos: Int, data: ChatMessageObject,
            eventListener: OnItemEventListener,
            isMultiSelect: Boolean,
            otherUserImage: String,
            otherUserName: String,
        ) {
            binder.txtDate.text = data.time
            Glide.with(itemView.context).load(data.message).into(binder.imgVideoFrame)

            binder.imgUser.loadImageWithGlide(otherUserImage, ImageBorderOption.CIRCLE)
            binder.txtUserName.text = otherUserName



            binder.imgUser.loadImageWithGlide(otherUserImage, ImageBorderOption.CIRCLE)
          /*  if (isMultiSelect) {
                binder.imgCheck.visibility = View.VISIBLE
                binder.imgCheck.messageSelectionToggle(data.isSelected)
            } else {
                binder.imgCheck.visibility = View.GONE
            }*/
            binder.root.setOnLongClickListener {
                eventListener.onLongPressListener(pos)
                return@setOnLongClickListener true
            }
            binder.root.setOnClickListener {
                if (isMultiSelect) {
                    data.isSelected = !data.isSelected
                   /* if (data.isSelected) {
                        binder.imgCheck.messageSelectionToggle(true)
                    } else {
                        binder.imgCheck.messageSelectionToggle(false)
                    }*/
                    eventListener.onSmallPressListener(pos)
                } else {
                    eventListener.onFilesListListener(
                        arrayListOf(
                            FileData(
                                data.message,
                                data.key,
                                data.deleteStatus
                            )
                        ), false
                    )
                }
            }
        }
    }

    class ImageListSenderHolder(val binder: ItemChatMessageImageListSenderBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun bind(
            pos: Int, data: ChatMessageObject, eventListener: OnItemEventListener,
            isMultiSelect: Boolean
        ) {

            binder.txtDate.text = data.time

            Log.d(TAG, "bind: testFLowImageListData660>>START")
            data.filesArray?.forEachIndexed { index, fileData ->
                Log.d(TAG, "bind: testFLowImageListData660>>$index>>$fileData")
            }

            UpdateImageGridUseCase().updateData(binder, data)


            //   eventListener.onIMageDowanlodeListener(data.filesArray ?: ArrayList(), true)


            binder.root.setOnLongClickListener {
                eventListener.onLongPressListener(pos)
                return@setOnLongClickListener true
            }
            binder.root.setOnClickListener {
                if (isMultiSelect) {
                    data.isSelected = !data.isSelected

                    eventListener.onSmallPressListener(pos)
                } else {
                    eventListener.onFilesListListener(data.filesArray ?: ArrayList(), true)
                }
            }
        }
    }


    class ImageListReceiverHolder(
        val binder: ItemChatMessageImageListReceiverBinding,
    ) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(
            pos: Int, data: ChatMessageObject, eventListener: OnItemEventListener,
            isMultiSelect: Boolean,
            otherUserImage: String,
            otherUserName: String,
        ) {
            binder.txtDate.text = data.time

            Log.d(TAG, "bind: testFLowImageListData695>>START")
            data.filesArray?.forEachIndexed { index, fileData ->
                Log.d(TAG, "bind: testFLowImageListData695>>$index>>$fileData")
            }

            UpdateImageGridUseCase().updateData(binder, data)
            Log.d(
                TAG,
                "bindviefsdhs: " + data.message + "data.data" + data.date + "data.fileArray" + data.filesArray!!.size
            )


            binder.imgUser.loadImageWithGlide(otherUserImage, ImageBorderOption.CIRCLE)
            binder.txtUserName.text = otherUserName


            binder.root.setOnLongClickListener {
                eventListener.onLongPressListener(pos)
                return@setOnLongClickListener true
            }
            binder.root.setOnClickListener {
                if (isMultiSelect) {
                    data.isSelected = !data.isSelected

                    eventListener.onSmallPressListener(pos)
                } else {
                    eventListener.onFilesListListener(data.filesArray ?: ArrayList(), false)
                }

            }
        }
    }

    class DateHolder(val binder: ItemChatMessageDateBinding) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(data: ChatMessageObject) {

            binder.txtDate.text = getTimeAgoFromDateOnly(data.date)
        }
    }

    class InvitationHolder(
        val binder: ItemAcceptanceBinding,
    ) :
        RecyclerView.ViewHolder(binder.root) {
        fun bind(data: ChatMessageObject) {
            binder.btnyes.setOnClickListener(View.OnClickListener {

            })

        }
    }

    class BlankHolder(itemBinding: ItemBlankBinding) : RecyclerView.ViewHolder(itemBinding.root)


}