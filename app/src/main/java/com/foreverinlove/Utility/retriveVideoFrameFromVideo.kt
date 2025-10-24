package com.foreverinlove.utility

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import com.foreverinlove.objects.TempUserDataObject

@Throws(Throwable::class)
fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {

    val data: TempUserDataObject? = null
    var bitmap: Bitmap? = null
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        if (Build.VERSION.SDK_INT >= 14) mediaMetadataRetriever.setDataSource(
            videoPath,
            HashMap()
        ) else mediaMetadataRetriever.setDataSource(videoPath)
        //   mediaMetadataRetriever.setDataSource(videoPath);
        bitmap = mediaMetadataRetriever.frameAtTime
    } catch (e: Exception) {
        e.printStackTrace()
       // throw Throwable(data?.profile_video + e.message)
    } finally {
        mediaMetadataRetriever?.release()
    }
    return bitmap
}