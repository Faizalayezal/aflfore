package com.foreverinlove.chatflow

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.foreverinlove.Constant
import com.foreverinlove.MediaListActivity
import com.foreverinlove.R
import com.foreverinlove.chatmodual.*
import com.foreverinlove.chatmodual.FormatDateUseCase.getTimeAgoFromLong
import com.foreverinlove.databinding.ActivityPersonalChatBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.DiscoverData
import com.foreverinlove.network.response.SwipeData
import com.foreverinlove.network.response.ViewedMeData
import com.foreverinlove.objects.ChatMessageObject
import com.foreverinlove.objects.FileData
import com.foreverinlove.objects.FileDownloadStatus
import com.foreverinlove.screen.activity.DetailProfileScreenActivity
import com.foreverinlove.screen.activity.MainActivity
import com.foreverinlove.screen.activity.ReportUserActivity
import com.foreverinlove.screen.activity.SubscriptionPlanActivity
import com.foreverinlove.singlevideocall.IncomingCallActivity
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("InflateParams")
@AndroidEntryPoint
class PersonalChatActivity : BaseActivity() {
    private var binding: ActivityPersonalChatBinding? = null
    private lateinit var msgList: ArrayList<ChatMessageObject>
    private lateinit var adapter: ChatAdepter

    private var tempOtherUserData: ViewedMeData? = null

    private val viewModelReportUnMatch: ReportUnmatchViewModel by viewModels()
    private val viewModelReportList: ReportListViewmodel by viewModels()


    private val myChatViewModel: ChatViewModel by viewModels()

    // private val viewModel: ChatViewModel by viewModels()

    private val RequestViewModel: ConfrimPrivateChatViewModel by viewModels()
    private val RejectedViewModel: RejectedPrivateChatViewModel by viewModels()
    private val DetailsViewModel: OpenDetailsViewModel by viewModels()

    private var fabOpen = false

    private var currentUserId: Int = 0
    private var otherUserId: Int = 0
    private var UserId: Int = 0
    private var matchId: Int = 0
    private var otherUserImage: String = ""
    private var otherUserName: String = ""
    private var privateChatTime: String = ""
    private var privateChatRequest: String = ""
    private var txtRequest: String = ""
    private var paymentStatus: String = ""
    private var currantUsrPlan: Boolean? = true
    private var otherplanStatus: Boolean? = true
    var fromRead: String? = null
    var bhaibhai: String? = null

    private val SECOND_ACTIVITY_REQUEST_CODE = 0

    private val multiFilePicker: MultiFilePicker by lazy {
        MultiFilePicker(this, filePickerListener)
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        screenOpened("Chat1to1")
        val imageid = intent.getSerializableExtra("matchData2") as? SwipeData?
        viewModelReportUnMatch.start()
        RequestViewModel.start()
        RejectedViewModel.start()
        DetailsViewModel.start()


        intent?.apply {
            currentUserId = getIntExtra("currentUserId", 0)
            otherUserId = getIntExtra("otherUserId", 0)
            UserId = getIntExtra("PRIVATEUserId", 0)
            otherUserImage = getStringExtra("otherUserImage") ?: ""
            otherUserName = getStringExtra("otherUserName") ?: ""
            matchId = getIntExtra("matchId", 0)
            privateChatTime = getStringExtra("privateChatTime") ?: ""
            privateChatRequest = getStringExtra("receiver") ?: ""
            txtRequest = getStringExtra("requestmsg") ?: ""
            currantUsrPlan = getBooleanExtra("planStatus", false)
            otherplanStatus = getBooleanExtra("otherplanStatus", false)
            fromRead = intent.getStringExtra("IsRead") ?: ""
            bhaibhai = intent.getStringExtra("matchMathi") ?: ""


        }

        binding?.edChat?.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)

        Log.d("TAG", "onCr127eat131e21: " + imageid+"--->>"+bhaibhai)

        if(bhaibhai=="bhaibhai"){

            otherUserId=imageid?.matched_user_id?.toIntOrNull()?:0
            Log.d("TAG", "onCreate312: "+imageid?.matched_user_id)
            myChatViewModel.getLastSeenDetails(imageid?.matched_user_id?:"", onlineListener)

        }


        Log.d("TAG", "onCr127eate: " + currantUsrPlan)
        Log.d("TAG", "onCr127eat131e21: " + imageid)

        if (txtRequest != "") {

            Glide.with(this@PersonalChatActivity).load(otherUserImage).into(binding!!.imgUser)
            binding?.txtUserDetails?.text =
                "$otherUserName has sent you private chat request, Do you want to accept?"

            binding?.txtMessage?.text = txtRequest

            try {
                //z time now zulu time 2022-11-29T12:43:05.000000Z
                val dateStr = privateChatTime
                val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                df.setTimeZone(TimeZone.getTimeZone("UTC"))
                val date: Date = df.parse(dateStr) as Date

                val newdf = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
                val formattedDate: String = newdf.format(date)
                binding?.txtTime?.text = formattedDate

            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        myChatViewModel.start(
            currentUserId,
            otherUserId,
            matchId.toString(),
            ::onSessionExpired,
            fromRead ?: ""
        )

        when (privateChatRequest) {
            "accepted" -> {
                /* intent?.apply {
                     privateChatTime = getStringExtra("privateChatTime") ?: ""
                     privateChatRequest = getStringExtra("receiver") ?: ""
                     txtRequest = getStringExtra("requestmsg") ?: ""

                 }*/
                myChatViewModel.chatFlow(true)
            }

            "requested" -> {

                binding?.rcvChatMessageList?.visibility = View.GONE
                binding?.requestpopup?.visibility = View.VISIBLE

                binding?.edChat?.isEnabled = false
                binding?.edChat?.isFocusable = false
                binding?.edChat?.isClickable = false

                binding?.emoji?.isEnabled = false
                binding?.emoji?.isFocusable = false
                binding?.emoji?.isClickable = false

                binding?.attachment?.isEnabled = false
                binding?.attachment?.isFocusable = false
                binding?.attachment?.isClickable = false

                binding?.imgOption?.visibility = View.GONE
                binding?.imgStartVideoCall?.visibility = View.GONE
                binding?.profile?.isFocusable = false
                binding?.profile?.isClickable = false
                binding?.profile?.isEnabled = false

                binding?.imgsend?.isFocusable = false
                binding?.imgsend?.isClickable = false
                binding?.imgsend?.isEnabled = false

                showToast("requested")
            }

            "rejected" -> {
                onBackPressed()
                showToast("Allready Rejected")
            }
        }

        /*  if (currentUserId == 0 || otherUserId == 0
          ) {
              showToast("Data not found")
              finish()
          }*/

        Glide.with(this@PersonalChatActivity).load(otherUserImage).into(binding!!.userprofile)
        binding?.txtOtherUserName?.text = otherUserName


        myChatViewModel.getLastSeenDetails(otherUserId.toString(), onlineListener)




        binding?.imgBack?.setOnClickListener {
            Constant.matchIdListener?.onOnDataMatchIdListenerSelected(matchId.toString())
            onBackPressed()
        }
        binding?.rcvChatMessageList?.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            closeFab()
        }



        binding?.llTopBarMain?.setOnClickListener {
            closeFab()
        }

        binding?.edChat?.addTextChangedListener {
            if (it.toString() == "") {
                binding?.attachment?.visibility = View.VISIBLE
            } else {
                binding?.attachment?.visibility = View.GONE
                closeFab()
                Log.d("TAG", "onCreatesddf: "+adapter.itemCount)
               // binding?.rcvChatMessageList?.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }




        binding?.imgOption?.setOnClickListener {
            binding?.imgStartVideoCall?.visibility = View.INVISIBLE
            binding?.imgOption?.setImageResource(R.mipmap.menu2)

            PopupWindow(binding?.imgOption?.context).apply {
                isOutsideTouchable = true
                val inflater = LayoutInflater.from(binding?.imgOption?.context)

                contentView = inflater.inflate(R.layout.dialog_report_unmatch, null).apply {
                    measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                }

                setOnDismissListener {
                    binding?.imgStartVideoCall?.visibility = View.VISIBLE
                    binding?.imgOption?.setImageResource(R.mipmap.menu)
                }
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                contentView.findViewById<TextView>(R.id.txtReport).setOnClickListener {
                    dismiss()
                    val intent = Intent(this@PersonalChatActivity, ReportUserActivity::class.java)
                    intent.putExtra("otherUserId", otherUserId.toString())
                    startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)

                }
                contentView.findViewById<TextView>(R.id.txtUnmatched).setOnClickListener {
                    dismiss()

                    val dialog = Dialog(this@PersonalChatActivity, android.R.style.Theme_Light)
                    dialog.window!!.setBackgroundDrawable(
                        ColorDrawable(
                            ContextCompat.getColor(
                                this@PersonalChatActivity,
                                R.color.transparent
                            )
                        )
                    )
                    dialog.window!!.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    )


                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.dialog_unmatch)
                    val llTop = dialog.findViewById<NestedScrollView>(R.id.llTop)
                    llTop.setOnClickListener { dialog.dismiss() }
                    val btnUnmatch = dialog.findViewById<AppCompatButton>(R.id.btnUnmatch)
                    val btnUnmatch2 = dialog.findViewById<AppCompatButton>(R.id.btnUnmatch2)
                    val btnCancel = dialog.findViewById<TextView>(R.id.btnCancel)

                    val emojiUnicode = "\u263A"
                    btnUnmatch.text = "Sorry,\nNot a connection \nBest of luck"+""+emojiUnicode

                    btnUnmatch.setOnClickListener {
                        dialog.dismiss()
                        viewModelReportUnMatch.callApiData(
                            (otherUserId.toString()),
                            "aa",
                            "unmatch"
                        )

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                    btnUnmatch2.setOnClickListener {
                        dialog.dismiss()
                        viewModelReportUnMatch.callApiData(
                            (otherUserId.toString()),
                            "aa",
                            "unmatch"
                        )

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                    btnCancel.setOnClickListener { dialog.dismiss() }
                    dialog.setOnDismissListener {
                        binding?.imgStartVideoCall?.visibility = View.VISIBLE
                        binding?.imgOption?.setImageResource(R.mipmap.menu)
                    }

                    dialog.show()

                }
            }.also { popupWindow ->
                // Absolute location of the anchor view
                val location = IntArray(2).apply {
                    binding?.imgOption?.getLocationOnScreen(this)
                }
                val size = Size(
                    popupWindow.contentView.measuredWidth,
                    popupWindow.contentView.measuredHeight
                )
                popupWindow.showAtLocation(
                    binding?.imgOption,
                    Gravity.TOP or Gravity.START,
                    location[0] - (size.width - binding!!.imgOption.width) / 2 - 100,
                    location[1] + size.height - 150
                )

            }
            closeFab()
        }

        binding?.imgStartVideoCall?.setOnClickListener {
            Log.d("TAG", "onCredffdfsate334: " + currantUsrPlan)
            Log.d("TAG", "onCredffdfsate335: " + otherplanStatus)
            if (currantUsrPlan == true) {
                openVideoCallDailog(1)
            } else {
                if (otherplanStatus == true) {
                    openVideoCallDailog(2)
                } else {
                    val intent = Intent(this@PersonalChatActivity, IncomingCallActivity::class.java)
                    intent.putExtra("otherUserId", otherUserId)
                    intent.putExtra("otherUserName", otherUserName)
                    intent.putExtra("otherUserImage", otherUserImage)
                    startActivity(intent)
                }
            }




            closeFab()
        }


        closeFab()


        binding?.attachment?.setOnClickListener {
            if (!fabOpen) {
                showFabOpen()
            } else {
                closeFab()
            }
        }
        binding?.emoji?.setOnClickListener {
            val emojiIcon =
                EmojIconActions(
                    this@PersonalChatActivity,
                    binding?.root,
                    binding?.edChat,
                    binding?.emoji
                )
            emojiIcon.ShowEmojIcon()
        }

        binding?.imgsend?.setOnClickListener {
            if (binding?.edChat?.text.toString().trim() != "") {
                myChatViewModel.sendMessage(binding?.edChat?.text.toString().trim())
                binding?.edChat?.setText("")
               // hideKeyboard()
                //  hideReplyLayout()
            }
        }
        adapter = ChatAdepter(multiListener, otherUserImage)
        binding?.rcvChatMessageList?.adapter = adapter

        myChatViewModel.msgConversion.observe(this) {
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

                for (messageObject in it) {
                    Log.d(
                        "bbb",
                        "onCreate: items>>>" + messageObject.message + ">>" + messageObject.type.intType
                    )
                }

            }
        }

        myChatViewModel.getMessageList()

        val layoutListener = ViewTreeObserver.OnGlobalLayoutListener { // Get the height of the keyboard
            try{
                binding?.rcvChatMessageList?.smoothScrollToPosition(adapter.itemCount - 1)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding?.rcvChatMessageList?.viewTreeObserver?.addOnGlobalLayoutListener(layoutListener)

        binding?.video?.setOnClickListener {
            multiFilePicker.startVideoPicker()
        }

        binding?.camera?.setOnClickListener {
            multiFilePicker.startImagePicker()
        }


        comformListener()
        rejectedListener()
        detailsOpenListner()

        binding?.txtIgnore?.setOnClickListener {
            RejectedViewModel.callApiRejectedData(UserId.toString())

        }

        binding?.txtAccept?.setOnClickListener {
            RequestViewModel.callApiConfirmData(otherUserId.toString())

        }
        val data: DiscoverData? = null

        binding?.profile?.setOnClickListener {

            DetailsViewModel.callApiOpenDetails(otherUserId.toString())

        }


    }


    private fun detailsOpenListner() {
        lifecycleScope.launch {
            DetailsViewModel.opneDetailsConversion.collect {
                when (it) {
                    OpenDetailsViewModel.OpenDetailsResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is OpenDetailsViewModel.OpenDetailsResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                    }

                    is OpenDetailsViewModel.OpenDetailsResponseEvent.Loading -> {
                        showProgressBar()

                    }

                    is OpenDetailsViewModel.OpenDetailsResponseEvent.Success -> {
                        Utility.hideProgressBar()



                        if (it.result.status == 1) {
                            val intent = Intent(
                                this@PersonalChatActivity,
                                DetailProfileScreenActivity::class.java
                            ).putExtra("btnVisibility", true)


                            intent.putExtra("userDetailsdatapersonalChat", it.result.data)


                            startActivity(intent)


                        } else if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }
    }


    private fun rejectedListener() {
        lifecycleScope.launch {
            RequestViewModel.confirmConversion.collect {
                when (it) {
                    ConfrimPrivateChatViewModel.ConfirmRequestResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is ConfrimPrivateChatViewModel.ConfirmRequestResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        // binding?.userRecy?.visibility = View.GONE
                    }

                    is ConfrimPrivateChatViewModel.ConfirmRequestResponseEvent.Loading -> {
                        showProgressBar()

                        // binding?.userRecy?.visibility = View.VISIBLE
                    }

                    is ConfrimPrivateChatViewModel.ConfirmRequestResponseEvent.Success -> {
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

    private fun comformListener() {
        lifecycleScope.launch {
            RejectedViewModel.rejectConversion.collect {
                when (it) {
                    RejectedPrivateChatViewModel.RejectedRequestResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is RejectedPrivateChatViewModel.RejectedRequestResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                        showToast(it.errorText)

                        // binding?.userRecy?.visibility = View.GONE
                    }

                    is RejectedPrivateChatViewModel.RejectedRequestResponseEvent.Loading -> {
                        showProgressBar()

                        // binding?.userRecy?.visibility = View.VISIBLE
                    }

                    is RejectedPrivateChatViewModel.RejectedRequestResponseEvent.Success -> {
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


    private val filePickerListener = object : MultiFilePicker.OnMultiFileListener {
        override fun onImagesSelected(bitmaps: ArrayList<Uri>) {
            closeFab()

            lifecycleScope.launch(Dispatchers.IO) {
                myChatViewModel.sendFiles(
                    bitmaps, ChatViewModel.SendFileType.Images

                )
            }
        }

        override fun onVideosSelected(videoList: ArrayList<Uri>) {

            closeFab()

            lifecycleScope.launch(Dispatchers.IO) {
                myChatViewModel.sendFiles(
                    videoList, ChatViewModel.SendFileType.Videos
                )
            }
        }

        override fun onFilesSelected(videoList: ArrayList<Uri>) {
            /*lifecycleScope.launch(Dispatchers.IO) {
                  viewModel.sendFiles(
                      videoList, ChatViewModel.SendFileType.Files
                  )
              }*/
        }
    }

    private fun onSessionExpired() {
        handleSessionExpired()
    }


    private fun closeFab() {
        fabOpen = false
        /*  binding?.video?.animate()?.translationX(+resources.getDimension(R.dimen.standerd_70))
          lifecycleScope.launch {
              delay(150)
              binding?.camera?.animate()?.translationY(-resources.getDimension(R.dimen.standerd_0))
              binding?.video?.animate()?.translationY(-resources.getDimension(R.dimen.standerd_0))
          }
          binding?.video?.visibility = View.GONE
          binding?.camera?.visibility = View.GONE*/
        binding?.attachOpen?.visibility = View.GONE


    }

    private fun showFabOpen() {

        fabOpen = true
        /* binding?.video?.visibility = View.VISIBLE
         binding?.camera?.visibility = View.VISIBLE
         binding?.camera?.animate()?.translationY(-resources.getDimension(R.dimen.standerd_70))
         binding?.video?.animate()?.translationY(-resources.getDimension(R.dimen.standerd_70))
         lifecycleScope.launch {
             delay(150)
             binding?.video?.animate()?.translationX(+resources.getDimension(R.dimen.standerd_140))

         }*/

        binding?.attachOpen?.visibility = View.VISIBLE


    }

    private val multiListener = object : ChatAdepter.OnMultiSelectChangeListener {
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
            Log.d("TAG", "onFilesLfffistOpen: " + list)
            val intent = Intent(this@PersonalChatActivity, MediaListActivity::class.java)
                .putExtra("isSender", isSender)
                .putExtra("currentUserId", currentUserId)
                .putExtra("otherUserId", otherUserId)
                .putExtra("list", list)
                .putExtra("name", binding?.txtOtherUserName?.text.toString())
                .putExtra("image", otherUserImage)
                .putExtra("matchId", matchId)


            resultLauncher.launch(intent)
        }

        override fun onSingleFileOpen(pos: Int, message: String) {
            when (msgList[pos].fileDownloadStatus) {
                FileDownloadStatus.NotDownloaded -> {
                    downloadSingleFile(message, msgList[pos].key)
                }

                FileDownloadStatus.Downloaded -> {
                    ChatMessageScreenHelper.openFile(Uri.parse(message), this@PersonalChatActivity)
                }

                FileDownloadStatus.Downloading -> {
                    showToast("Download is in progress.")
                }
            }
        }


        override fun scrollToBottom() {
            try {
                binding?.rcvChatMessageList?.smoothScrollToPosition(adapter.itemCount - 1)
            } catch (e: Exception) {

            }
        }

    }
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                myChatViewModel.msgConversion.value?.clear()
                myChatViewModel.getMessageList()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                finish()

            }
        } else {
            multiFilePicker.attachActivityResult(requestCode, resultCode, data)
        }
    }

    private val fileDownloadHelper by lazy {
        FileDownloadHelper(this, false)
    }

    private fun downloadSingleFile(it: String, id: String) {
        val fileType = try {
            Constant.getFileName(it).split(".")[1]
        } catch (e: Exception) {
            ""
        }

        val newFile = File(
            id = "${System.currentTimeMillis()}",
            name = Constant.getFileName(it),
            type = fileType,
            url = it,
            downloadedUri = null
        )

        fileDownloadHelper.startDownloadingFile(newFile, {

            for (i in msgList.indices) {
                if (msgList[i].key == id) {

                    val pathStr = Constant.getFilePath(
                        Constant.getFileName(msgList[i].message),
                        filesDir.absolutePath
                    )

                    if (java.io.File(pathStr).exists()) {
                        msgList[i].message = pathStr
                    }
                    msgList[i].fileDownloadStatus = FileDownloadStatus.Downloaded
                }
            }
            adapter.updateData(msgList)
            // Log.d(TAG, "onCreate: test success>>$it")
        }, {
            for (i in msgList.indices) {
                if (msgList[i].key == id) {
                    msgList[i].fileDownloadStatus = FileDownloadStatus.NotDownloaded
                }
            }
            adapter.updateData(msgList)
            //    Log.d(TAG, "onCreate: test failed>>$it")
        }, {
            for (i in msgList.indices) {
                if (msgList[i].key == id) {
                    msgList[i].fileDownloadStatus = FileDownloadStatus.Downloading
                }
            }
            adapter.updateData(msgList)
            //  Log.d(TAG, "onCreate: test running>>")
        })
    }

    private val onlineListener = object : ChatViewModel.OnOnlineStatusListener {
        @SuppressLint("SetTextI18n")
        override fun onStatusChanged(data: OnlineStatusObj) {
            val lastTime = System.currentTimeMillis()
            Log.d("TAG", "onStatusChanged: testvaluechanged>>" + data.status + ">>" + data.lastSeen)
            Log.d("TAG", "onStatusChanged: "+data)

            if (data.status == "1") {
                binding?.txtLastSeenStatus?.text = "Online"
            }else  {
                binding?.txtLastSeenStatus?.text =
                    "Last seen ${(data.lastSeen ?: "0").toLong().getTimeAgoFromLong()}"
                //  binding?.txtLastSeenStatus?.text ="Last seen ${(data.lastSeen ?: "0")+lastTime})}"
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binding = null
    }

    private fun openVideoCallDailog(user: Int) {
        val dialog = Dialog(this@PersonalChatActivity, R.style.successfullDailog)
        dialog.setContentView(R.layout.dailogvideocall)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this@PersonalChatActivity,
                    R.color.sucessaplaytransperent
                )
            )
        )
        val Planbutton = dialog.findViewById<AppCompatButton>(R.id.btnBrowsePlan)
        val PlanText = dialog.findViewById<TextView>(R.id.txtnext)

        if (user == 1) {
            Planbutton.visibility = View.VISIBLE
        } else {
            Planbutton.visibility = View.GONE
            PlanText.text =
                "Sorry video call cannot be initiated because the other person is using a Free Plan."
        }


        Glide.with(this@PersonalChatActivity).load(R.mipmap.videoempty)
            .into(dialog.findViewById<ImageView>(R.id.imageView13))
        Planbutton.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this@PersonalChatActivity, SubscriptionPlanActivity::class.java))
        }


        dialog.findViewById<ConstraintLayout>(R.id.dimiss).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


}







