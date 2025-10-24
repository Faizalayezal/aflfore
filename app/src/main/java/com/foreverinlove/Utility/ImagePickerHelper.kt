package com.foreverinlove.utility

import android.app.Activity
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import com.foreverinlove.Constant
import java.io.File
import java.net.URI

class ImagePickerHelper(private val fileUri: Uri, val activity: Activity) {
    private var bitmapTop:Bitmap?=null

    fun getBitmap(): Bitmap?{
        try {
            var bitmap = MediaStore.Images.Media.getBitmap(
                activity.contentResolver,
                fileUri
            )

            try {
                val juri = URI(fileUri.toString())

                val ei = ExifInterface(File(juri).absolutePath)
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

                bitmap =
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> Constant.rotateImage(bitmap, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> Constant.rotateImage(bitmap, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> Constant.rotateImage(bitmap, 270f)
                        ExifInterface.ORIENTATION_NORMAL -> bitmap
                        else -> bitmap
                    }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            bitmapTop=bitmap
            return bitmapTop
        } catch (e: Exception) {
            //handle exception
            e.printStackTrace()
        }

        return bitmapTop
    }
}