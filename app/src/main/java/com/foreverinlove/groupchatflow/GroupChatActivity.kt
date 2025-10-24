package com.foreverinlove.groupchatflow

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.MediaListActivity
import com.foreverinlove.R
import com.foreverinlove.adapter.ImageGroupListAdapter
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.chatmodual.MultiFilePicker
import com.foreverinlove.databinding.ActivityGroupChatBinding
import com.foreverinlove.groupvideocall.GroupVideoCallActivity
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.DiscoverData
import com.foreverinlove.network.response.RoomList
import com.foreverinlove.network.response.RoomMemberList
import com.foreverinlove.network.response.ViewedMeData
import com.foreverinlove.objects.*
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.hideKeyboard
import com.foreverinlove.utility.ActivityExt.showToast
import dagger.hilt.android.AndroidEntryPoint
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class GroupChatActivity : BaseActivity() {
    private var isallshoing = false
    private var fabOpen = false
    private lateinit var msgList: ArrayList<ChatMessageObject>
    private val SECOND_ACTIVITY_REQUEST_CODE = 0

    private val dataDetails: DiscoverData? = null

    /*
        private val listData = listOf(
            NewUserList(R.mipmap.img1, true, "Gaurav"),
            NewUserList(R.mipmap.img2, true, "Faizal"),
            NewUserList(R.mipmap.img3, false, "Dip"),
            NewUserList(R.mipmap.img4, true, "Jaggu"),
            NewUserList(R.mipmap.img1, false, "Dip"),
            NewUserList(R.mipmap.img2, false, "Sohel"),

            )
    */

    private lateinit var adapter: GroupChatAdepter

    private lateinit var imageGroupListAdapter: ImageGroupListAdapter
    private val viewModelTop: GroupChatViewModel by viewModels()
    var roomData: RoomList? = null

    var CurrantUseTime: String? = null
    var GroupTime: String? = null

    var tempUserDataObject: TempUserDataObject? = null
    private val multiFilePicker: MultiFilePicker by lazy {
        MultiFilePicker(this, filePickerListener)
    }


    private lateinit var binding: ActivityGroupChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelTop.leavestart()
        screenOpened("ChatGroup")
        binding.hori.isHorizontalScrollBarEnabled = false

        roomData = intent.getSerializableExtra("roomData") as? RoomList?

        val roomid = roomData?.room_id
        val grouppic1 = roomData?.room_icon
        val grouppic2 = roomData?.room_icon1
        val grouname = roomData?.room_name
        val CurrantUseName = roomData?.room_join_member?.firstOrNull()?.user?.first_name
        CurrantUseTime = roomData?.room_join_member?.firstOrNull()?.user?.created_at
        GroupTime = roomData?.created_at


        viewModelTop.start(
            viewModelTop.tempUserDataObject?.id?.toIntOrNull() ?: 0,
            roomid ?: 0,
            roomid.toString(),
            ::onSessionExpired,
            roomData
        )

        binding.edChat.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)


        Glide.with(this@GroupChatActivity).load(grouppic1).into(binding.groupprofile1)
        Glide.with(this@GroupChatActivity).load(grouppic2).into(binding.groupprofile2)
        binding.groupname.text = grouname



        binding.groupStartVideoCall.setOnClickListener {
            startActivity(
                Intent(applicationContext, GroupVideoCallActivity::class.java)
                    .putExtra("startCall", true)
                    .putExtra("roomId", (roomData?.room_id ?: 0).toString())
                    .putExtra("roomName", (roomData?.room_name ?: 0).toString())
                    .putExtra("CurrantuserName", CurrantUseName)
            )
        }


        binding.imgBack.setOnClickListener {
            onBackPressed()
        }


        binding.edChat.addTextChangedListener {
            if (it.toString() == "") {
                binding.attachment.visibility = View.VISIBLE
            } else {
                binding.attachment.visibility = View.GONE
                closeFab()

            }
        }
        binding.imgsend.setOnClickListener {
            if (binding.edChat.text.toString().trim() != "") {
                viewModelTop.sendMessage(binding.edChat.text.toString().trim())
                binding.edChat.setText("")
               // hideKeyboard()
                //  hideReplyLayout()
            }
        }

        adapter = GroupChatAdepter(multiListener)
        binding.rcvChatMessageList.adapter = adapter

        viewModelTop.msgConversion.observe(this) {
            it?.let {
                msgList = it

                it.lastOrNull()?.let {

                    Log.d("TAG", "onCreate: testLastItemData>>++++++++++++++++++++++++++++++++")
                    Log.d(
                        "TAG",
                        "onCreate: testLastItemData>>" + it.message + ">>" + it.filesArray?.size
                    )
                    it.filesArray?.forEach {
                        Log.d(
                            "TAG",
                            "onCreate: testLastItemData>>Inside>>" + it.fileKey + ">>" + it.fileString
                        )
                    }
                }

                adapter.updateData(msgList)

                // val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                // var currentDate = sdf.format(Date())

                for (messageObject in it) {
                    Log.d(
                        "bbb",
                        "onCreate: items>>>" + messageObject.message + ">>" + messageObject.type.intType
                    )

                    //  Log.d("TAG", "onasdsdsdCreate: "+currentDate)

                }

                /* if ((CurrantUseTime ?: "") > (GroupTime.toString())){

                 }*/


            }
        }
        viewModelTop.getMessageList()

        val layoutListener = ViewTreeObserver.OnGlobalLayoutListener { // Get the height of the keyboard
            try{
                binding.rcvChatMessageList.smoothScrollToPosition(adapter.itemCount - 1)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.rcvChatMessageList.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)


        binding.attachment.setOnClickListener {
            if (!fabOpen) {
                showFabOpen()
            } else {
                closeFab()
            }
        }

        binding.emoji.setOnClickListener {
            val emojiIcon =
                EmojIconActions(
                    this@GroupChatActivity,
                    binding.root,
                    binding.edChat,
                    binding.emoji
                )
            emojiIcon.ShowEmojIcon()
        }

        binding.rcvChatMessageList.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            closeFab()
        }
        binding.llTopBarMain.setOnClickListener {
            closeFab()
        }

        binding.imgExit.setOnClickListener {
            SuccessExitDialog(roomData?.room_id ?: 0)
        }
        closeFab()


        binding.showuser.setOnClickListener {
            binding.showuser.text = "Show more"

            if (isallshoing) {
                isallshoing = false


            } else {
                isallshoing = true
                binding.showuser.text = "Show less"


            }

            showUserList(roomData?.room_join_member ?: listOf(), isallshoing)


        }

        binding.video.setOnClickListener {
            multiFilePicker.startVideoPicker()
        }


        binding.camera.setOnClickListener {
            multiFilePicker.startImagePicker()
        }
        showUserList(roomData?.room_join_member ?: listOf(), isallshoing)
        ApiListner()
        privateChatListner()
    }


    private fun onSessionExpired() {
        handleSessionExpired()
    }

    private fun closeFab() {
        fabOpen = false
        /* binding.video.animate().translationX(+resources.getDimension(R.dimen.standerd_70))
         lifecycleScope.launch {
             delay(150)
             binding.camera.animate()?.translationY(-resources.getDimension(R.dimen.standerd_0))
             binding.video.animate()?.translationY(-resources.getDimension(R.dimen.standerd_0))
         }
         binding.video.visibility = View.GONE
         binding.camera.visibility = View.GONE*/

        binding.attachOpen.visibility = View.GONE


    }

    private fun showFabOpen() {

        fabOpen = true
        /* binding.video.visibility = View.VISIBLE
         binding.camera.visibility = View.VISIBLE
         binding.camera.animate()?.translationY(-resources.getDimension(R.dimen.standerd_70))
         binding.video.animate()?.translationY(-resources.getDimension(R.dimen.standerd_70))
         lifecycleScope.launch {
             delay(150)
             binding.video.animate()?.translationX(+resources.getDimension(R.dimen.standerd_140))

         }*/

        binding.attachOpen.visibility = View.VISIBLE

    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                viewModelTop.msgConversion.value?.clear()
                viewModelTop.getMessageList()
            }
        }

    private val multiListener = object : GroupChatAdepter.OnMultiSelectChangeListener {
        override fun onMultiChange(isMultiSelect: Boolean) {
            if (isMultiSelect) {
                // toggleMultiSelectView(true)
            } else {
                // toggleMultiSelectView(false)
            }
        }

        override fun onSelectedCountChange(count: Int, list: ArrayList<ChatMessageObject>) {
        }

        override fun onFilesListOpen(list: ArrayList<FileData>, isSender: Boolean) {
            val intent = Intent(this@GroupChatActivity, MediaListActivity::class.java)
                .putExtra("isSender", isSender)
                .putExtra("currentUserId", roomData?.room_id)
                .putExtra(
                    "otherUserId",
                    roomData?.room_join_member?.firstOrNull()?.user?.user_images?.firstOrNull()?.id
                        ?: ""
                )
                .putExtra("list", list)
                //  .putExtra("name", binding.txtOtherUserName?.text.toString())
                .putExtra(
                    "image",
                    roomData?.room_join_member?.firstOrNull()?.user?.user_images?.firstOrNull()?.url
                        ?: ""
                )
                .putExtra("matchId", roomData?.room_id)


            resultLauncher.launch(intent)
        }

        override fun onSingleFileOpen(pos: Int, message: String) {
            /*  when (msgList[pos].fileDownloadStatus) {
                  FileDownloadStatus.NotDownloaded -> {
                      downloadSingleFile(message, msgList[pos].key)
                  }
                  FileDownloadStatus.Downloaded -> {
                      ChatMessageScreenHelper.openFile(Uri.parse(message), this@PersonalChatActivity)
                  }
                  FileDownloadStatus.Downloading -> {
                      showToast("Download is in progress.")
                  }
              }*/
        }


        override fun scrollToBottom() {
            try {
                binding.rcvChatMessageList.smoothScrollToPosition(adapter.itemCount - 1)
            } catch (e: Exception) {

            }
        }

    }


    private fun ApiListner() {
        lifecycleScope.launch {
            viewModelTop.leaveConversion.collect {
                when (it) {
                    GroupChatViewModel.LeaveRoomResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GroupChatViewModel.LeaveRoomResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        // binding?.userRecy?.visibility = View.GONE
                    }
                    is GroupChatViewModel.LeaveRoomResponseEvent.Loading -> {
                        showProgressBar()

                        // binding?.userRecy?.visibility = View.VISIBLE
                    }
                    is GroupChatViewModel.LeaveRoomResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            onBackPressed()
                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }

    }

    private fun privateChatListner() {
        lifecycleScope.launch {
            viewModelTop.privateChatConversion.collect {
                when (it) {
                    GroupChatViewModel.PrivateChatResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }
                    is GroupChatViewModel.PrivateChatResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        // binding?.userRecy?.visibility = View.GONE
                    }
                    is GroupChatViewModel.PrivateChatResponseEvent.Loading -> {
                        showProgressBar()

                        // binding?.userRecy?.visibility = View.VISIBLE
                    }
                    is GroupChatViewModel.PrivateChatResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == 1) {
                            showToast("Request sent successfully")

                        } else if (it.result.message == "This User already rejected your request") {
                            showToast("This User already rejected your request")

                        } else if (it.result.message == "You have already received their request") {
                            showToast("You have already received their request")

                        } else if (it.result.message == "User already match with this profile") {
                            showToast("User already added in private chat")


                        } else if (it.result.message == "Request Already sent") {
                            showToast("Request Already sent")


                        } else if (it.result.message == "Please Upgrade Your Account To Make Private chat") {
                            showToast("Please Purchase your plan To Make Private chat")


                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }

    }

    private val filePickerListener = object : MultiFilePicker.OnMultiFileListener {
        override fun onImagesSelected(bitmaps: ArrayList<Uri>) {
            closeFab()

            lifecycleScope.launch(Dispatchers.IO) {
                viewModelTop.sendFiles(
                    bitmaps, GroupChatViewModel.SendFileType.Images

                )
            }
        }

        override fun onVideosSelected(videoList: ArrayList<Uri>) {

            closeFab()

            lifecycleScope.launch(Dispatchers.IO) {
                viewModelTop.sendFiles(
                    videoList, GroupChatViewModel.SendFileType.Videos
                )
            }
        }

        override fun onFilesSelected(videoList: ArrayList<Uri>) {
            /*lifecycleScope.launch(Dispatchers.IO) {
                  viewModelTop.sendFiles(
                      videoList, GroupChatViewModel.SendFileType.Files
                  )
              }*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                finish();

            }
        } else {
            multiFilePicker.attachActivityResult(requestCode, resultCode, data)
        }
    }


    private fun showUserList(listData: List<RoomMemberList>, isallshowing: Boolean) {
        var newEmptyList = ArrayList<RoomMemberList>()
        if (isallshowing) {
            newEmptyList.addAll(listData)
        } else {
            newEmptyList = ArrayList(listData.take(3))
        }

        imageGroupListAdapter = ImageGroupListAdapter(
            this@GroupChatActivity,
            Listner,
            viewModelTop.tempUserDataObject?.id?.toIntOrNull() ?: 0,
            newEmptyList
        )

        viewModelTop.getOnlineStatusOfUsers(newEmptyList) {
            imageGroupListAdapter.replesList(it)

        }
        binding.recychatuser.adapter = imageGroupListAdapter


    }

    private var tempOtherUserData: ViewedMeData? = null
    private val Listner = object : ImageGroupListAdapter.onClick {


        override fun OpenPop(data: RoomMemberList, position: Int) {
            tempOtherUserData = data.user
            if (data.user_id == viewModelTop.tempUserDataObject?.id) {
                showToast("You")
            } else {
                personalChatopen(data.user_id ?: "", data.user?.first_name.toString())
            }
        }

    }

    private fun SuccessExitDialog(roomid: Int) {

        val dialog = Dialog(this@GroupChatActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dialogexit)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        )

        val imgView = dialog.findViewById<ImageView>(R.id.imageView13)
        Glide.with(this@GroupChatActivity).load(R.mipmap.exitbg)
            .into(dialog.findViewById<ImageView>(R.id.imageView12))
        Glide.with(this@GroupChatActivity)
            .load(ContextCompat.getDrawable(this@GroupChatActivity, R.drawable.extraperson))
            .into(imgView)


        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.quite).setOnClickListener {
            dialog.dismiss()
            viewModelTop.callLeaveRoomApi(roomid.toString())
            viewModelTop.deleteUseDelete(viewModelTop.tempUserDataObject?.id?.toInt() ?: 0)


        }

        dialog.show()
    }

    private fun personalChatopen(uid: String, userName: String) {
        val dialog = Dialog(this@GroupChatActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.item_acceptance)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@GroupChatActivity,
                    R.color.white
                )
            )
        )

        dialog.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.strtxt).text = "Private request send to $userName"

        dialog.findViewById<Button>(R.id.btnyes).setOnClickListener {
            if (dialog.findViewById<EditText>(R.id.edmsg).text.toString() == "") {
                showToast("please enter the message")

            } else {
                val usermsg = dialog.findViewById<EditText>(R.id.edmsg).text.toString()
                viewModelTop.callPrivateChatApi(uid, usermsg)
            }

            dialog.dismiss()
        }


        dialog.show()
    }


}


