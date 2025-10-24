package com.foreverinlove

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.Constant.getFileName
import com.foreverinlove.Constant.getFilePath
import com.foreverinlove.Constant.isUrlVideo
import com.foreverinlove.chatflow.ChatViewModel
import com.foreverinlove.chatmodual.*
import com.foreverinlove.chatmodual.FormatDateUseCase.getTimeAgoFromLong
import com.foreverinlove.databinding.ActivityMediaListBinding
import com.foreverinlove.network.Utility
import com.foreverinlove.objects.FileData
import com.foreverinlove.utility.ActivityExt.handleSessionExpired
import com.foreverinlove.utility.ActivityExt.showToast


private const val TAG = "MediaListActivity"

class MediaListActivity : BaseActivity() {

    enum class FileStatus {
        NotDownload,
        Downloading,
        Downloaded
    }

    private val fileDownloadHelper by lazy {
        FileDownloadHelper(this, true)
    }

    data class MediaObject(var string: String, var isUrl: Boolean, var key: String,var deleteStatus:String)

    private var binder: ActivityMediaListBinding?=null

    private var filesList = ArrayList<MediaObject>()
    private var listFileStatus = ArrayList<FileStatus>()
    private var name = ""
    private var otherUserId = 0
    private var currentUserId = 0
    private var image = ""
    private var matchId = 0
    private var isSender = false
    var fromRead: String? = null
    private val myChatViewModel: ChatViewModel by viewModels()

    private lateinit var mediaAdapter: ImageVerticalAdepter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE)
        binder = ActivityMediaListBinding.inflate(layoutInflater)
        setContentView(binder?.root)

        intent?.apply {
            try {
                val newList = getSerializableExtra("list") as (ArrayList<FileData>)
                Log.d(TAG, "onCreate231: "+newList)

                newList.forEach {
                    filesList.add(MediaObject(it.fileString, true, it.fileKey,it.deleteStatus))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            currentUserId = getIntExtra("currentUserId",0)
            otherUserId = getIntExtra("otherUserId",0)
            name = getStringExtra("name") ?: ""
            image = getStringExtra("image") ?: ""
            matchId = getIntExtra("matchId",0)
            isSender = getBooleanExtra("isSender",false)
            fromRead = intent.getStringExtra("IsRead") ?: ""

        }

        myChatViewModel.start(currentUserId,otherUserId,matchId.toString(),::onSessionExpired,fromRead?:"")
        myChatViewModel.getLastSeenDetails(otherUserId.toString(), onlineListener)


        binder?.apply {
            PagerSnapHelper().attachToRecyclerView(rcvMediaList)

            imgBack.setOnClickListener { onBackPressed() }

            if (currentUserId == 0 && otherUserId == 0) {
                imgDownloadState.visibility = View.GONE
            } else {
                imgDownloadState.visibility = View.VISIBLE
            }


            imgDownloadState.setOnClickListener {
                if (listFileStatus.isEmpty()) return@setOnClickListener

                if (listFileStatus[currentPosTop] == FileStatus.NotDownload) {
                    downloadSingleFile(filesList[currentPosTop].string, currentPosTop)
                } else if (listFileStatus[currentPosTop] == FileStatus.Downloaded) {
                    showToast("File is already downloaded.")
                } else if (listFileStatus[currentPosTop] == FileStatus.Downloading) {
                    showToast("File is downloading.")
                }

            }
        }



        setupScreen()
    }

    private fun onSessionExpired(){
        handleSessionExpired()
    }

    private var isDataDeleted=false

    private fun setupScreen() {
        filesList.forEachIndexed { index, item ->
            var tempFileName = getFileName(item.string)
            tempFileName = if (isUrlVideo(getFileName(item.string))) {
                "$tempFileName.mp4"
            } else {
                "$tempFileName.png"
            }

            if (checkIfFileExist(tempFileName)) {
                filesList[index].isUrl = false

                filesList[index].string = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath+"/" + tempFileName

                    Log.d(TAG, "checkIfFileExist: te4stFilePath>>$path")

                    java.io.File(path).isFile
                    path
                } else {
                    val target = java.io.File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        tempFileName
                    )


                    target.isFile
                    target.path
                }

                (getFilePath(tempFileName,filesDir.absolutePath))

                listFileStatus.add(FileStatus.Downloaded)
            } else {
                listFileStatus.add(FileStatus.NotDownload)
            }
        }

        setListData()

        listFileStatus.firstOrNull()?.let {
            setCurrentFileStatus(it)
        }
    }

    private val onlineListener = object : ChatViewModel.OnOnlineStatusListener {
        @SuppressLint("SetTextI18n")
        override fun onStatusChanged(data: OnlineStatusObj) {


            if (data.status == "1") {
              //  binder.txtLastSeenStatus.text = "Online"
            } else {
              //  binder.txtLastSeenStatus.text =
                    "Last seen ${(data.lastSeen ?: "0").toLong().getTimeAgoFromLong()}"
            }

        }
    }

    private fun setListData() {

        binder?.rcvMediaList?.apply {
            mediaAdapter = ImageVerticalAdepter(filesList)


            layoutManager =
                LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
            this.adapter = mediaAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    var isNewItem = false

                    val pos =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (pos != currentPosTop) {
                        isNewItem = true
                    }
                    currentPosTop = pos
                    setCurrentFileStatus(listFileStatus[currentPosTop])

                    if (isNewItem) mediaAdapter.checkIsItemPlaying()
                }
            })
        }

    }

    override fun onBackPressed() {

        if(isDataDeleted) {
            setResult(RESULT_OK)
            finish()
        }else{
            super.onBackPressed()
        }
    }

    override fun onPause() {
        mediaAdapter.checkIsItemPlaying()

        super.onPause()

    }

    private var currentPosTop = 0

    private fun setCurrentFileStatus(fileStatus: FileStatus) {
        when (fileStatus) {
            FileStatus.NotDownload -> {
                binder?.imgDownloadState?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.file_download
                    )
                )
            }
            FileStatus.Downloading -> {
                binder?.imgDownloadState?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.file_downloading
                    )
                )
            }
            FileStatus.Downloaded -> {
                binder?.imgDownloadState?.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.file_download_done
                    )
                )
            }
        }
    }

    private fun downloadSingleFile(it: String, pos: Int) {
        val isVideo = isUrlVideo(it)

        val fileType = if (isVideo) "MP4" else "PNG"

        val newFile = File(
            id = "${System.currentTimeMillis()}",
            name = getFileName(it),
            type = fileType,
            url = it,
            downloadedUri = null
        )

        fileDownloadHelper.startDownloadingFile(newFile, {
            listFileStatus[pos] = FileStatus.Downloaded
            setCurrentFileStatus(listFileStatus[currentPosTop])

            val ab = java.io.File(getRealPathFromURI(this, Uri.parse(it))?:"")

        }, {
            listFileStatus[pos] = FileStatus.NotDownload
            setCurrentFileStatus(listFileStatus[currentPosTop])
        }, {
            listFileStatus[pos] = FileStatus.Downloading
            setCurrentFileStatus(listFileStatus[currentPosTop])
        })
    }
    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }


    private fun checkIfFileExist(fileName: String): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath+"/" + fileName


            java.io.File(path).isFile
        } else {
            val target = java.io.File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )


            target.isFile
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.hideProgressBar()
        binder=null
    }


}