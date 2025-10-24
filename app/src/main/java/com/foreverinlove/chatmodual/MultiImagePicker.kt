package com.foreverinlove.chatmodual

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.foreverinlove.Constant
import com.foreverinlove.network.GetUriFileSizeUseCase
import com.foreverinlove.network.LongToMbSizeUseCase
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.ImagePickerHelper
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class MultiFilePicker(private val activity: Activity, private val listener: OnMultiFileListener) {

    private var pickImageCode = 1
    private var pickVideoCode = 2
    private var pickFileCode = 3
    private var bitmapArray = ArrayList<Uri>()
    fun startImagePicker() {
        bitmapArray.clear()

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        activity.startActivityForResult(intent, pickImageCode)
    }

    fun startVideoPicker() {
        bitmapArray.clear()

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.type = "video/mp4"
        activity.startActivityForResult(intent, pickVideoCode)

    }

    // fun startFilePicker() {
    //    bitmapArray.clear()

    //    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    //    intent.addCategory(Intent.CATEGORY_OPENABLE)
    //    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
    //    intent.type = "*/*"
    //    activity.startActivityForResult(intent, pickFileCode)

    // }*/

    fun attachActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == pickImageCode && resultCode == Activity.RESULT_OK && null != data) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount

                if (count > 10) {
                    activity.showToast("Maximum 10 files can be selected at once.")
                    return
                }

                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    val helper = ImagePickerHelper(imageUri, activity)
                    var bitmap = helper.getBitmap()
                    if (bitmap != null) {
                        bitmap = Constant.scaleBitmap(bitmap, 999999)
                        if (bitmap != null) {
                            val uri = getImageUri(bitmap)
                            if (uri != null) bitmapArray.add(uri)
                        }
                    }
                }
                listener.onImagesSelected(bitmapArray)
            } else if (data.data != null) {
                val imageUri: Uri = data.data!!
                val helper = ImagePickerHelper(imageUri, activity)
                var bitmap = helper.getBitmap()

                Log.d("TAG", "attachActivityResult: test90>>" + data.data)

                if (bitmap != null) {
                    bitmap = Constant.scaleBitmap(bitmap, 999999)
                    bitmap?.let {
                        Log.d("TAG", "attachActivityResult: test95>>" + bitmap.byteCount)
                        val uri = getImageUri(bitmap)
                        if (uri != null) bitmapArray.add(uri)
                    }
                    Log.d("TAG", "attachActivityResult: test98>>" + bitmap?.byteCount)
                }
                listener.onImagesSelected(bitmapArray)
            }
        } else if (requestCode == pickVideoCode && resultCode == Activity.RESULT_OK && null != data) {

            val result: ArrayList<Uri> = ArrayList()
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                if (count > 10) {
                    activity.showToast("Maximum 10 files can be selected at once.")
                    return
                }
                for (i in 0 until count) {
                    /*val uri = data.clipData!!.getItemAt(i).uri
                    val file = File(uri.path ?: "")
                    val split = file.path.split(":")
                    val filePath = split[1]
                    Log.d("TAG", "attachActivityResult: testvideolist>1>$filePath")*/

                    try {
                        result.add(data.data!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            } else {
                /*val uri = data.data!!
                val file = File(uri.path)
                val split = file.path.split(":")
                val filePath = split[1]*/
                data.data?.let {

                    val mbLength = LongToMbSizeUseCase().convert(
                        GetUriFileSizeUseCase().length(it, activity.contentResolver)
                    )

                    if (mbLength > 15) {
                        activity.showToast("File cannot exceed 15 mb size.")
                    } else result.add(data.data!!)

                }
            }

            listener.onVideosSelected(result)
        } else if (requestCode == pickFileCode && resultCode == Activity.RESULT_OK && null != data) {

            val result: ArrayList<Uri> = ArrayList()
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                if (count > 10) {
                    activity.showToast("Maximum 10 files can be selected at once.")
                    return
                }
                for (i in 0 until count) {
                    /*val uri = data.clipData!!.getItemAt(i).uri
                    val file = File(uri.path ?: "")
                    val split = file.path.split(":")
                    val filePath = split[1]
                    Log.d("TAG", "attachActivityResult: testvideolist>1>$filePath")*/
                    try {
                        result.add(data.data!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            } else {
                /*val uri = data.data!!
                val file = File(uri.path)
                val split = file.path.split(":")
                val filePath = split[1]*/

                data.data?.let {

                    val mbLength = LongToMbSizeUseCase().convert(
                        GetUriFileSizeUseCase().length(it, activity.contentResolver)
                    )

                    if (mbLength > 15) {
                        activity.showToast("File cannot exceed 15 mb size.")
                    } else result.add(data.data!!)

                }


            }

            listener.onFilesSelected(result)
        }

    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveImageInQ(inImage)
        else saveTheImageLegacy(inImage)
    }

    @SuppressLint("InlinedApi")
    private fun saveImageInQ(bitmap: Bitmap): Uri? {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream?
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //use application context to get contentResolver
        val contentResolver = activity.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        imageUri?.let { contentResolver.update(it, contentValues, null, null) }

        return imageUri
    }

    private fun saveTheImageLegacy(bitmap: Bitmap): Uri? {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, "" + System.currentTimeMillis() + "file")
        val fos = FileOutputStream(image)
        fos.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }

        return Uri.fromFile(image)
    }

    interface OnMultiFileListener {
        fun onImagesSelected(bitmaps: ArrayList<Uri>)
        fun onVideosSelected(videoList: ArrayList<Uri>)
        fun onFilesSelected(videoList: ArrayList<Uri>)
    }

    private fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = true

        try {


            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)

                    val newId = getId(uri)

                    Log.d("TAG", "getPath: testabdkfjdklf>>$newId")
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), (newId?.toLong() ?: 0)
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getId(uri: Uri): String? {
        val id = DocumentsContract.getDocumentId(uri)
        if (!TextUtils.isEmpty(id)) {
            return if (id.startsWith("raw:")) {
                id.replaceFirst("raw:".toRegex(), "")
            } else try {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                getDataColumn(activity, contentUri, null, null)
            } catch (e: java.lang.NumberFormatException) {
                null
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

}