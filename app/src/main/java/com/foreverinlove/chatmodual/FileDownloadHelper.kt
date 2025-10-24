package com.foreverinlove.chatmodual

import android.app.Activity
import androidx.work.*
import com.foreverinlove.MediaListActivity
import com.foreverinlove.chatflow.PersonalChatActivity


class FileDownloadHelper(private val activity: Activity, private val isMediaAct:Boolean) {
    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(activity)
    }

    internal fun startDownloadingFile(
        file: File,
        success: (String) -> Unit,
        failed: (String) -> Unit,
        running: () -> Unit
    ) {
        val data = Data.Builder()

        data.apply {
            putString(FileDownloadWorker.FileParams.KEY_FILE_NAME, file.name)
            putString(FileDownloadWorker.FileParams.KEY_FILE_URL, file.url)
            putString(FileDownloadWorker.FileParams.KEY_FILE_TYPE, file.type)
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val fileDownloadWorker = OneTimeWorkRequestBuilder<FileDownloadWorker>()
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()

        workManager.enqueueUniqueWork(
            "oneFileDownloadWork_${System.currentTimeMillis()}",
            ExistingWorkPolicy.KEEP,
            fileDownloadWorker
        )

        val thisact=if(isMediaAct)activity as MediaListActivity
        else activity as PersonalChatActivity

        workManager.getWorkInfoByIdLiveData(fileDownloadWorker.id)
            .observe(thisact) { info ->
                info?.let {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {

                            success(
                                it.outputData.getString(FileDownloadWorker.FileParams.KEY_FILE_URI)
                                    ?: ""
                            )
                        }
                        WorkInfo.State.FAILED -> {
                            failed("Downloading failed!")
                        }
                        WorkInfo.State.RUNNING -> {
                            running()
                        }
                        else -> {
                            failed("Something went wrong")
                        }
                    }
                }
            }
    }
}