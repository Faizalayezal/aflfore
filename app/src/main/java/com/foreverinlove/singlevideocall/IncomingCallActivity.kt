package com.foreverinlove.singlevideocall

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.foreverinlove.Constant.attachMovableView
import com.foreverinlove.R
import com.foreverinlove.chatmodual.BaseActivity
import com.foreverinlove.chatmodual.ImageViewExt.loadImageWithGlide
import com.foreverinlove.chatmodual.ImageViewExt.setDrawable
import com.foreverinlove.databinding.ActivityIncomingCallBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.network.Utility.showSnackBar
import com.foreverinlove.network.Utility.showProgressBar
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.NotificationFlowHandler
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class IncomingCallActivity : BaseActivity() {
    private var mRtcEngine: RtcEngine? = null
    private var otherUserImage: String = ""
    private var otherUserName: String = ""
    private var otherUserId: Int = 0
    private var isVideoShowing = true
    private var isAudioShowing = true
    private val agora_app_id = "201681993f2645039a223768fff5001c"
    private var isFirstTime = true
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadAudioPermissionGranted = false
    private var isCameraPermissionGranted = false
    private var isNotificationPermissionGranted = false
    val permissionRequest: MutableList<String> = ArrayList()

    private val viewModel: VideoCallViewModel by viewModels()

    //same vara ne notification jay
    private val notificationFlowHandler: NotificationFlowHandler by lazy {
        NotificationFlowHandler().apply {
            fetchNormalNotificationData(intent)
        }
    }

    private lateinit var binding: ActivityIncomingCallBinding
    private var isCallReceived = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.start()
        screenOpened("VideoCall")
        intent?.apply {
            otherUserId = getIntExtra("otherUserId", 0)
            otherUserImage = getStringExtra("otherUserImage") ?: ""
            otherUserName = getStringExtra("otherUserName") ?: ""


        }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                if (permission.isEmpty()) {
                    isReadAudioPermissionGranted =
                        permission[android.Manifest.permission.RECORD_AUDIO]
                            ?: isReadAudioPermissionGranted
                    isCameraPermissionGranted =
                        permission[android.Manifest.permission.CAMERA] ?: isCameraPermissionGranted

                    isNotificationPermissionGranted =
                        permission[android.Manifest.permission.POST_NOTIFICATIONS]
                            ?: isNotificationPermissionGranted
                } else {
                    Log.d("TAG", "sdsdfsdfsdfs: " + 998)
                    initAgoraEngineAndJoinChannel()

                }

            }


        requestPermission()
        var tempchannel_name = ""
        var tempsender_u_id = ""
        var tempreaciver_token = ""

        //call recive thy with notification throw
        when (val intentData = notificationFlowHandler.flowTracker) {
            is NotificationFlowHandler.FlowTracker.FirstTimeSingleVideoCall -> {
                otherUserId = (intentData.data.user_id ?: 0)
                tempchannel_name = intentData.data.channel_name ?: ""
                tempsender_u_id = intentData.data.receiver_u_id ?: ""
                tempreaciver_token = intentData.data.reaciver_token ?: ""

                otherUserImage = intentData.data.user_image?.firstOrNull()?.url ?: ""
                otherUserName = intentData.data.user_name ?: ""

                isCallReceived = true

            }

            null -> Unit

            else -> Unit
        }


        binding.remotevideoviewcontainer.setOnTouchListener(attachMovableView())

        binding.apply {
            offcall.setOnClickListener {
                finish()
            }
            offvideo.setOnClickListener {
                isVideoShowing = !isVideoShowing

                if (isVideoShowing) {
                    // mRtcEngine?.enableVideo()
                    mRtcEngine?.enableLocalVideo(true)
                    binding.noVideoShadeCurrentUser.visibility = View.GONE
                    binding.offvideo.setDrawable(R.drawable.video_solid_white)
                    binding.imgToggleVideo.setDrawable(R.drawable.video_solid_white)
                } else {
                    //  mRtcEngine?.disableVideo()
                    mRtcEngine?.enableLocalVideo(false)
                    binding.noVideoShadeCurrentUser.visibility = View.VISIBLE
                    binding.offvideo.setDrawable(R.drawable.ic_video_slash_solid)
                    binding.imgToggleVideo.setDrawable(R.drawable.ic_video_slash_solid)
                }

            }
            imgToggleAudio.setOnClickListener {
                offaudio.performClick()
            }
            imgToggleVideo.setOnClickListener {
                offvideo.performClick()
            }

            offaudio.setOnClickListener {
                isAudioShowing = !isAudioShowing

                if (isAudioShowing) {
                    //  mRtcEngine?.enableAudio()
                    mRtcEngine?.enableLocalAudio(true)
                    binding.offaudio.setDrawable(R.drawable.microphone_solid_white)
                    binding.imgToggleAudio.setDrawable(R.drawable.microphone_solid_white)
                } else {
                    //  mRtcEngine?.disableAudio()
                    mRtcEngine?.enableLocalAudio(false)
                    binding.offaudio.setDrawable(R.drawable.ic_microphone_lines_slash_solid)
                    binding.imgToggleAudio.setDrawable(R.drawable.ic_microphone_lines_slash_solid)
                }
            }

            llDetailActions.visibility = View.VISIBLE
            // llDetailActions.animate().translationY(-resources.getDimension(R.dimen.minusstanderd_4))

            binding.imgBg.loadImageWithGlide(otherUserImage)

            // binding.txtName.text = otherUserName
            binding.txtName.text = otherUserName




            flipcemra.setOnClickListener {
                mRtcEngine?.switchCamera()

            }


        }


        binding.imgEndVideoCall.setOnClickListener {

            if (isCallReceived) {
                setMode(VideoCallStatus.Connected)
                callVideoCall(
                    channel_name = tempchannel_name,
                    sender_u_id = tempsender_u_id,
                    sender_token = tempreaciver_token,
                )

            } else {
                finish()
            }
        }

        setMode(VideoCallStatus.Ringing)


        if (isCallReceived) {

        } else {

            if (permissionRequest.isNotEmpty()) {
            } else {
                viewModel.callApiData(otherUserId.toString(), "1")
                ListnerVideoCall()
            }

        }
    }

    var job: Job? = null

    private fun requestPermission() {
        isReadAudioPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        isCameraPermissionGranted =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        isNotificationPermissionGranted =
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) ==
                    PackageManager.PERMISSION_GRANTED

        if (!isReadAudioPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.RECORD_AUDIO)
            Log.d("TAG", "requasasestPermission: " + 254)
            showSnackBar(
                this,
                "Microphone permission blocked",
                this,
                findViewById(R.id.maainlayout),
                getColor(R.color.splashbg)
            )

        }
        if (!isCameraPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.CAMERA)
            Log.d("TAG", "requasasestPermission: " + 259)

        }
        if (!isNotificationPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
            Log.d("TAG", "requasasestPermission: " + 264)
            showSnackBar(
                this,
                "Notification blocked",
                this,
                findViewById(R.id.maainlayout),
                getColor(R.color.splashbg)
            )

        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        } else {

            initAgoraEngineAndJoinChannel()

        }
    }

    private fun initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine()
        setupVideoProfile()
        setupLocalVideo()
        joinChannel(
            token = strToken,
            uid = strUid,
            channelName = strChannalName,
        )


    }

    /*
        private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
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
    */


    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

         when (requestCode) {
             PERMISSION_REQ_ID_RECORD_AUDIO -> {
                 if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
                 } else {
                     showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO)
                     finish()
                 }
             }

             PERMISSION_REQ_ID_CAMERA -> {
                 if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel()
                 } else {
                     showLongToast("No permission for " + Manifest.permission.CAMERA)
                     finish()
                 }
             }
             PERMISSION_REQ_ID_NOTIFICATION -> {
                 if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     initAgoraEngineAndJoinChannel()
                 } else {
                     showLongToast("No permission for " + Manifest.permission.POST_NOTIFICATIONS)
                     finish()
                 }
             }
         }
    }*/

    private fun showLongToast(msg: String) =
        this.runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }


    private fun setupRemoteVideo(uid: Int) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        val container = findViewById<FrameLayout>(R.id.localvideoviewcontainer)


        val surfaceView = RtcEngine.CreateRendererView(this@IncomingCallActivity)
        // val surfaceView = SurfaceViewRenderer(this@IncomingCallActivity)

        //  container.addView(surfaceView)
        container.addView(
            surfaceView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // Initializes the video view of a remote user. RENDER_MODE_HIDDEN
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        binding.llCallActions.visibility = View.GONE


    }

    private fun onRemoteUserLeft() {
        lifecycleScope.launch {
            showToast("User disconnected")
            delay(3000)
            finish()
        }
    }

    private fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {
        val container = findViewById<FrameLayout>(R.id.localvideoviewcontainer)

        val surfaceView = container.getChildAt(0) as SurfaceView

        val tag = surfaceView.tag
        if (tag != null && tag as Int == uid) {
            surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
        }
    }

    private fun leaveChannel() {
        mRtcEngine?.leaveChannel()
    }


    private fun ListnerVideoCall() {
        lifecycleScope.launch {
            viewModel.videoCallConversion.collect {
                when (it) {
                    VideoCallViewModel.VideoCallResponseEvent.Empty -> {
                        Utility.hideProgressBar()
                    }

                    is VideoCallViewModel.VideoCallResponseEvent.Failure -> {
                        Utility.hideProgressBar()
                    }

                    VideoCallViewModel.VideoCallResponseEvent.Loading -> {
                        showProgressBar()
                    }

                    is VideoCallViewModel.VideoCallResponseEvent.Success -> {
                        Utility.hideProgressBar()
                        if (it.result.status == -2) {
                            handleSessionExpired()
                        } else {
                            callVideoCall(
                                sender_token = it.result.data?.sender_token ?: "",
                                sender_u_id = it.result.data?.sender_u_id ?: "",
                                channel_name = it.result.data?.channel_name ?: "",
                            )

                        }
                    }
                }
            }
        }
    }

    private var strToken: String? = null
    private var strUid: String? = null
    private var strChannalName: String? = null
    private fun callVideoCall(
        sender_token: String,
        sender_u_id: String,
        channel_name: String,
    ) {
        Log.d(
            "TAG",
            "callVideoCall: " + sender_token + "- ->" + sender_u_id + "- ->" + channel_name
        )
        strToken = sender_token
        strUid = sender_u_id
        strChannalName = channel_name


        /* if (checkSelfPermission(
                 Manifest.permission.RECORD_AUDIO,
                 PERMISSION_REQ_ID_RECORD_AUDIO
             ) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA) &&
             checkSelfPermission(
                 Manifest.permission.POST_NOTIFICATIONS,
                 PERMISSION_REQ_ID_NOTIFICATION
             )
         ) {
             initAgoraEngineAndJoinChannel()
         }*/
        if (!isReadAudioPermissionGranted) {
            Log.d("TAG", "callVideoCallsdffdsf: " + "RECORD_AUDIO")

            permissionRequest.add(android.Manifest.permission.RECORD_AUDIO)
        }
        if (!isCameraPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.CAMERA)
            Log.d("TAG", "callVideoCallsdffdsf: " + "CAMERA")

        }
        if (!isNotificationPermissionGranted) {
            permissionRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
            Log.d("TAG", "callVideoCallsdffdsf: " + "POST_NOTIFICATIONS")
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
            Log.d("TAG", "callVideoCallsdffdsf: " + "launch")

        } else {
            Log.d("TAG", "callVideoCallsdffdsf: " + "grant")

            initAgoraEngineAndJoinChannel()

        }
    }


    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, agora_app_id, mRtcEventHandler)
        } catch (e: Exception) {
            Log.e(LOG_TAG, Log.getStackTraceString(e))

            throw RuntimeException(
                "NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(
                    e
                )
            )
        }
    }

    private fun setupVideoProfile() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.]
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


    @SuppressLint("ResourceAsColor")
    private fun setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        val container = findViewById<FrameLayout>(R.id.remotevideoviewcontainer)
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        // val surfaceView = SurfaceViewRenderer(baseContext)

        surfaceView.setZOrderMediaOverlay(true)
        container.addView(surfaceView)
        // Initializes the local video view.
        // RENDER_MODE_FIT: Uniformly scale the video until one of its dimension fits the boundary. Areas that are not filled due to the disparity in the aspect ratio are filled with black.
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
            uid?.toIntOrNull() ?: 0
        ) // if you do not specify the uid, we will generate the uid for you
    }


    override fun onDestroy() {
        super.onDestroy()

        leaveChannel()

        RtcEngine.destroy()
        mRtcEngine = null
    }


    companion object {

        private val LOG_TAG = IncomingCallActivity::class.java.simpleName

        const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        const val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1
        const val PERMISSION_REQ_ID_NOTIFICATION = 23
        // const val PERMISSION_REQ_ID_CAMERA =24

    }

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        //user join thy tyare
        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("TAG", "onUserJoineuidd: 458" + uid)
            setMode(VideoCallStatus.Connected)
            runOnUiThread {
                setupRemoteVideo(uid)

            }
        }


        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread { onRemoteUserLeft() }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            runOnUiThread { onRemoteUserVideoMuted(uid, muted) }
        }

        @Deprecated("Deprecated in Java")
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread { setupRemoteVideo(uid) }
        }

    }


    private var lastSelectedMode: VideoCallStatus? = null
    private fun setMode(status: VideoCallStatus) {
        if (status == VideoCallStatus.Ringing && lastSelectedMode != status) {
            binding.apply {
                viewShade.visibility = View.GONE
                //imgCurrUser.visibility = View.GONE
                txtName.visibility = View.GONE
                txtCallStatus.visibility = View.GONE
                llCallActions.visibility = View.GONE

                floatingImg.visibility = View.GONE
                llDetailActions.visibility = View.GONE

                imgEndVideoCall.background = if (!isCallReceived) {
                    imgEndVideoCall.rotation = 0f
                    ContextCompat.getDrawable(applicationContext, R.drawable.red_circle)
                } else {
                    imgEndVideoCall.rotation = 220f
                    ContextCompat.getDrawable(applicationContext, R.drawable.green_circle)
                }

                viewShade.crossfade()
                //imgCurrUser.crossfade()
                txtName.crossfade()
                txtCallStatus.crossfade()
                llCallActions.crossfade()
            }

            disconnectIfUserDontAnswerCall()

        } else if (status == VideoCallStatus.Connected && lastSelectedMode != status) {
            userAnsweredCall = true
            binding.apply {
                viewShade.crossfade(true)
                //imgCurrUser.crossfade(true)
                txtName.crossfade(true)
                txtCallStatus.crossfade(true)
                //llCallActions.crossfade(true)
                llDetailActions.visibility = View.VISIBLE
                llCallActions.visibility = View.GONE
                binding.llDetailActions.crossfade()

            }
            lifecycleScope.launch {
                delay(time.toLong())
                binding.floatingImg.crossfade()
                binding.llDetailActions.crossfade()
            }
        }
        lastSelectedMode = status
    }


    private val disconnectDefaultDuration = 30_000L
    private var userAnsweredCall = false

    private fun disconnectIfUserDontAnswerCall() {
        lifecycleScope.launch {
            delay(disconnectDefaultDuration)
            if (!userAnsweredCall) {
                delay(3000)
                finish()
            }
        }
    }

    private val time = 1000
    private fun View.crossfade(inverse: Boolean = false) {
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

    enum class VideoCallStatus {
        Ringing,
        Connected
    }
}


