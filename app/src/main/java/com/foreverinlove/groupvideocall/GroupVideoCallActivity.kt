package com.foreverinlove.groupvideocall

import ai.deepar.ar.*
import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.Constant.attachMovableView
import com.foreverinlove.R
import com.foreverinlove.chatmodual.ImageViewExt.setDrawable
import com.foreverinlove.databinding.ActivityGroupVideoCallBinding
import com.foreverinlove.network.Utility.hideProgressBar
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.network.response.ConsumeGroupVideoCallRoomJoinMember
import com.foreverinlove.network.response.GetMemberListData
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.singlevideocall.IncomingCallActivity
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.NotificationFlowHandler
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.viewmodels.GroupVideoCallViewModel
import com.foreverinlove.viewmodels.VideoCallStatus
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "GroupVideoCallActivity"

@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class GroupVideoCallActivity : AppCompatActivity() {
    private val agora_app_id = "201681993f2645039a223768fff5001c"

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var defaultLensFacing: Int = CameraSelector.LENS_FACING_FRONT
    private var lensFacing = defaultLensFacing
    private var mRtcEngine: RtcEngine? = null
    var tempUserDataObject: TempUserDataObject? = null
    private var isVideoShowing = true
    private var isAudioShowing = true
    private var isFirstTime = true
    private lateinit var binding: ActivityGroupVideoCallBinding
    private val viewModel: GroupVideoCallViewModel by viewModels()
    private val notificationFlowHandler: NotificationFlowHandler by lazy {
        NotificationFlowHandler().apply {
            fetchNormalNotificationData(intent)
        }
    }
    var userIds: Int = 0
    var roomId = ""
    var groupName = ""
    var firstname = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getStringExtra("roomId") ?: ""
        groupName = intent.getStringExtra("roomName") ?: ""

        when (val intentData = notificationFlowHandler.flowTracker) {
            is NotificationFlowHandler.FlowTracker.FirstTimeGroupVideoCall -> {
                roomId = (intentData.data.room_id ?: 0).toString()
                //groupName = (intentData.data.room_name ?: 0).toString()
            }

            null -> Unit
            else -> {}
        }
        lifecycleScope.launch {
            dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject = it

                }
        }

        binding.txtName.text = groupName
        //  binding.currantUserName.text = tempUserDataObject?.first_name


        if (intent.getBooleanExtra("startCall", false)) {
            viewModel.startVideoCall(roomId)
        } else {
            //recive kre user call tyare pela aa call thy
            viewModel.consumeAgoraData(roomId)
        }

        binding.cons.setOnTouchListener(attachMovableView())


        viewModel.changeScreenState(VideoCallStatus.Ringing)
        setScreenState()
        setApiState()
        userEvents()



        getUserListApiListner()
        UserIdApiListner()


        loopApiCall(true)


    }

    @SuppressLint("SuspiciousIndentation")
    private fun getUserListApiListner() {
        lifecycleScope.launch {
            viewModel.memberListConversion.collect { data ->
                when (data) {
                    GroupVideoCallViewModel.GetMemberApiCallEvent.Empty -> hideProgressBar()
                    is GroupVideoCallViewModel.GetMemberApiCallEvent.Failure -> {
                        //  hideProgressBar()
                        showToast(data.errorText)

                    }

                    is GroupVideoCallViewModel.GetMemberApiCallEvent.Loading -> {
                        // showProgressBar()

                    }

                    is GroupVideoCallViewModel.GetMemberApiCallEvent.Success -> {
                        // hideProgressBar()
                        if (data.result.status == 1) {
                            Log.d(TAG, "4654s56gg: " + data.result.data)

                            val currentUidList = listAdapter?.getList()
                            Log.d(TAG, "afasfsad654654: " + currentUidList)

                            data.result.data?.forEach { uid ->
                                currentUidList?.find {
                                    Log.d(
                                        TAG,
                                        "getUserListApiListner13254: "
                                                + uid.u_id + "--->"
                                                + it.strUid.toString()
                                    )

                                    uid.u_id == it.strUid.toString()

                                }.let {
                                    Log.d(TAG, "bhiabhai: " + it)
                                    listAdapter?.updateItem(it?.u_id, it?.first_name)

                                }

                            }


                        }

                        if (data.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }

    }

    private fun UserIdApiListner() {
        lifecycleScope.launch {
            viewModel.updateIdsConversion.collect {
                when (it) {
                    GroupVideoCallViewModel.UpdateIdsApiCallEvent.Empty -> hideProgressBar()
                    is GroupVideoCallViewModel.UpdateIdsApiCallEvent.Failure -> {
                        //  hideProgressBar()
                        showToast(it.errorText)

                    }

                    is GroupVideoCallViewModel.UpdateIdsApiCallEvent.Loading -> {
                        //  showProgressBar()

                    }

                    is GroupVideoCallViewModel.UpdateIdsApiCallEvent.Success -> {
                        //  hideProgressBar()
                        if (it.result.status == 1) {

                        }

                        if (it.result.status == -2) {
                            handleSessionExpired()
                        }

                    }
                }
            }
        }

    }


    private fun setApiState() = lifecycleScope.launch {
        viewModel.apiCallConversion.collect {
            when (val apiData = it) {
                GroupVideoCallViewModel.ApiCallEvent.Empty -> hideProgressBar()
                is GroupVideoCallViewModel.ApiCallEvent.Failure -> hideProgressBar()
                GroupVideoCallViewModel.ApiCallEvent.Loading -> showProgressBar()
                is GroupVideoCallViewModel.ApiCallEvent.StartVideoCallSuccess -> {
                    hideProgressBar()

                    strToken = apiData.result.data?.consumed_user_data?.token
                    strUid = apiData.result.data?.consumed_user_data?.u_id
                    strChannalName = apiData.result.data?.consumed_user_data?.channel_name

                    if (checkSelfPermission(
                            Manifest.permission.RECORD_AUDIO,
                            IncomingCallActivity.PERMISSION_REQ_ID_RECORD_AUDIO
                        ) && checkSelfPermission(
                            Manifest.permission.CAMERA,
                            IncomingCallActivity.PERMISSION_REQ_ID_CAMERA
                        )
                    ) {
                        initAgoraEngineAndJoinChannel()
                    }
                    roomJoinMemberList = apiData.result.data?.room_joined_member?.room_join_member
                    apiData.result.data?.consumed_user_data?.apply {
                        Log.d(
                            TAG, "setApiState: testStartVideoCallFlow" +
                                    ">>$on_going_group_call_id" +
                                    ">>$channel_name" +
                                    ">>$u_id>>" +
                                    "$token"
                        )
                        onGoing = on_going_group_call_id
                    }
                }
            }
        }
    }

    private var roomJoinMemberList: List<ConsumeGroupVideoCallRoomJoinMember>? = null
    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i(TAG, "checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode)

        when (requestCode) {
            IncomingCallActivity.PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(
                        Manifest.permission.CAMERA,
                        IncomingCallActivity.PERMISSION_REQ_ID_CAMERA
                    )
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO)
                    finish()
                }
            }

            IncomingCallActivity.PERMISSION_REQ_ID_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel()
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA)
                    finish()
                }
            }
        }
    }

    private var strToken: String? = null
    private var strUid: String? = null
    private var strChannalName: String? = null
    private var onGoing: Int? = null
    private var isAlreadyStarted = false
    private fun initAgoraEngineAndJoinChannel() {
        if (isAlreadyStarted) return
        initializeAgoraEngine()
        setupVideoProfile()
        setupLocalVideo()
        joinChannel(
            token = strToken,
            uid = strUid,
            channelName = strChannalName,
        )

        isAlreadyStarted = true
    }

    private var listAdapter: GroupvideocallAdepter? = null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) = runOnUiThread {

            lifecycleScope.launch {
                userIds = uid
            }

            if (strUid == uid.toString()) {
                setupRemoteVideo(uid)
            } else {
                if (listAdapter == null) {
                    listAdapter = GroupvideocallAdepter(this@GroupVideoCallActivity, mRtcEngine)
                    binding.rcvGridList.adapter = listAdapter
                }


                /*listAdapter!!.addItem(
                    GetMemberListData(
                        uid.toString(),"",
                    )
                )*/
                Log.d(TAG, "onUserJoined341: " + strUid + "-->" + uid.toString())
                listAdapter!!.addItem(
                    GetMemberListData(
                        strUid?.toIntOrNull(), uid.toString(), "",
                    )
                )
            }
            viewModel.changeScreenState(VideoCallStatus.Connected)
        }

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            Log.d(TAG, "currantUid->2: " + uid)

            super.onFirstRemoteVideoDecoded(uid, width, height, elapsed)
            viewModel.changeScreenState(VideoCallStatus.Connected)
        }

        override fun onUserOffline(uid: Int, reason: Int) {

            Log.d(TAG, "currantUid->3: " + uid)

            Log.d(TAG, "onUserJoined: testFlowGroupCall>>onUserOffline>>${listAdapter?.itemCount}")
            val currentList = listAdapter?.getList()

            currentList?.forEach {
                Log.d(TAG, "onUserJoinedonUserOffline: testListItem>>${it.u_id?.toInt()}")
            }
            var count = currentList?.size ?: 0
//changes====================
            currentList?.find { it.u_id?.toInt() == uid }?.let {
                Log.d(TAG, "onUserJoinedonUserOffline: testItemRemoved>>$listAdapter")
                count -= 1
                runOnUiThread {
                    listAdapter?.removeItem(it)
                }
            }

            val currentListNew = listAdapter?.getList()

            Log.d(TAG, "onUserJoinedonUserOffline: testItemRemovedSize>>${currentListNew?.size}")

            if (count < 1) {
                lifecycleScope.launch(Dispatchers.Main) {
                    showToast("All users left.")
                    delay(2000)
                    finish()
                }
            }
            //runOnUiThread { onRemoteUserLeft() }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {

            Log.d(TAG, "currantUid->4: " + uid)
            Log.d(TAG, "onUserJoined: testFlowGroupCall>>onUserMuteVideo")
            //runOnUiThread { onRemoteUserVideoMuted(uid, muted) }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            Log.d(TAG, "currantUid->5: " + uid)
            viewModel.updateMemberData(roomId, uid.toString())
        }
    }


    private val apiCallDelaySec = 20
    private var job: Job? = null
    private fun loopApiCall(isStart: Boolean) {
        if (job?.isActive == true) job?.cancel()

        if (isStart) {
            job = lifecycleScope.launch {
                delay((apiCallDelaySec * 1000).toLong())
                viewModel.getMemberData(roomId)
                delay(5000)
                loopApiCall(true)
            }
        }
    }


    private fun setupRemoteVideo(uid: Int) {

        val container = findViewById<FrameLayout>(R.id.localvideoviewcontainer)
        if (container.childCount >= 1) return
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
       // val surfaceView = SurfaceViewRenderer(baseContext)

        container.addView(surfaceView)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))


    }

    /*private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {
        val container = findViewById<FrameLayout>(R.id.localvideoviewcontainer)
        val surfaceView = container.getChildAt(0) as SurfaceView
        val tag = surfaceView.tag
        if (tag != null && tag as Int == uid) {
            surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
        }
    }*/

    private fun initializeAgoraEngine() = try {
        mRtcEngine = RtcEngine.create(baseContext, agora_app_id, mRtcEventHandler)
    } catch (e: Exception) {
        Log.e(TAG, Log.getStackTraceString(e))
        throw RuntimeException(
            "NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(
                e
            )
        )
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            Log.d(TAG, "onStop1321: "+"stop")
            viewModel.endAudioCall(tempUserDataObject?.token ?: "", roomId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            viewModel.endAudioCall(tempUserDataObject?.token ?: "", roomId)
            Log.d(TAG, "onStop1321: "+"ondestory")
        }
        leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
    }

    private fun leaveChannel() = mRtcEngine?.leaveChannel()

    private fun setupVideoProfile() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        if (isFirstTime) {
            mRtcEngine!!.enableVideo()
            Log.d("TAG", "setupVideoProfile123: " + 231)
            isFirstTime = false
        }
//      mRtcEngine!!.setVideoProfile(Constants.VIDEO_PROFILE_360P, false) // Earlier than 2.3.0

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    private fun setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.


        val container = findViewById<FrameLayout>(R.id.localvideoviewcontainer)
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
       // val surfaceView = SurfaceViewRenderer(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel(
        token: String?,
        uid: String?,
        channelName: String?,
    ) {
        mRtcEngine!!.joinChannel(
            token,
            channelName,
            "Extra Optional Data",
            //uniqueUID
            uid?.toIntOrNull() ?: 0
        ) // if you do not specify the uid, we will generate the uid for you
    }

    private fun showLongToast(msg: String) =
        this.runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }

    private fun userEvents() {
        binding.imgPhonePick.setOnClickListener {
            lifecycleScope.launch {
                viewModel.endAudioCall(tempUserDataObject?.token ?: "", roomId)
                finish()
            }
        }
        binding.offvideo.setOnClickListener {
            isVideoShowing = !isVideoShowing

            if (isVideoShowing) {
                // mRtcEngine?.enableVideo()
                mRtcEngine?.enableLocalVideo(true)
                //  binding.noVideoShadeCurrentUser.visibility = View.GONE
                binding.offvideo.setDrawable(R.drawable.video_solid_white)
                binding.imgToggleVideo.setDrawable(R.drawable.video_solid_white)
            } else {
                // mRtcEngine?.disableVideo()
                mRtcEngine?.enableLocalVideo(false)
                //  binding.noVideoShadeCurrentUser.visibility = View.VISIBLE
                binding.offvideo.setDrawable(R.drawable.ic_video_slash_solid)
                binding.imgToggleVideo.setDrawable(R.drawable.ic_video_slash_solid)
            }

        }
        binding.offaudio.setOnClickListener {
            isAudioShowing = !isAudioShowing
            if (isAudioShowing) {
                //  mRtcEngine?.enableAudio()
                mRtcEngine?.enableLocalAudio(true)
                binding.offaudio.setDrawable(R.drawable.microphone_solid_white)
                binding.imgToggleAudio.setDrawable(R.drawable.microphone_solid_white)
            } else {
                // mRtcEngine?.disableAudio()
                mRtcEngine?.enableLocalAudio(false)
                binding.offaudio.setDrawable(R.drawable.ic_microphone_lines_slash_solid)
                binding.imgToggleAudio.setDrawable(R.drawable.ic_microphone_lines_slash_solid)
            }

        }
        binding.imgToggleAudio.setOnClickListener {
            binding.offaudio.performClick()
        }
        binding.imgToggleVideo.setOnClickListener {
            binding.offvideo.performClick()
        }
        binding.imgflipcemra.setOnClickListener {
            mRtcEngine?.switchCamera()
        }
        binding.offcall.setOnClickListener {
            lifecycleScope.launch {
                viewModel.endAudioCall(tempUserDataObject?.token ?: "", roomId)

                finish()
            }
        }
    }

    private fun setScreenState() = lifecycleScope.launch {
        viewModel.screenStateFlow.collect {
            when (it) {
                VideoCallStatus.Ringing -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.apply {
                            viewShade.visibility = View.GONE
                            //imgCurrUser.visibility = View.GONE
                            txtName.visibility = View.GONE
                            txtCallStatus.visibility = View.GONE
                            llCallActions.visibility = View.GONE

                            floatingImg.visibility = View.GONE
                            llDetailActions.visibility = View.GONE

                            viewShade.crossFade()
                            //imgCurrUser.crossfade()
                            txtName.crossFade()
                            txtCallStatus.crossFade()
                            llCallActions.crossFade()
                        }
                    }
                }

                VideoCallStatus.Connected -> {
                    cameraProviderFuture =
                        ProcessCameraProvider.getInstance(this@GroupVideoCallActivity)
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.apply {
                            viewShade.crossFade(true)
                            //imgCurrUser.crossfade(true)
                            txtName.crossFade(true)
                            txtCallStatus.crossFade(true)
                            llCallActions.crossFade(true)
                        }
                        lifecycleScope.launch {
                            delay(time.toLong())
                            binding.floatingImg.crossFade()
                            binding.llDetailActions.crossFade()
                        }
                    }
                }
            }
        }
    }
}

private const val time = 1000
private fun View.crossFade(inverse: Boolean = false) {
    if (inverse) {
        this.apply {
            alpha = 1f
            visibility = View.VISIBLE
            animate().alpha(0f).setDuration(time.toLong())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) = Unit

                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE

                    }
                    override fun onAnimationCancel(animation: Animator) = Unit

                    override fun onAnimationRepeat(animation: Animator) = Unit


                })
        }
    } else {
        this.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(time.toLong()).setListener(null)
        }
    }
}
