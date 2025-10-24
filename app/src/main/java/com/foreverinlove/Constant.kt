package com.foreverinlove

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.foreverinlove.objects.DiscoverFilterObject
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Math.floor
import java.lang.Math.sqrt
import java.util.regex.Pattern

interface OnDiscoverFilterListener {
    fun onFiltersSelected(data: DiscoverFilterObject)
}

interface OnDataMatchIdListener {
    fun onOnDataMatchIdListenerSelected(data: String)
}

interface dataPopUp {
    fun onData(data: String)
}


object Constant {
    var discoverListener: OnDiscoverFilterListener? = null
    var matchIdListener: OnDataMatchIdListener? = null
    var DataListner: dataPopUp? = null

    var lastFilterFragCloseTime = 0L
    var isDiscoverOpen = false
    var heightdata = MutableLiveData<String>()
    var savbtnClick = MutableLiveData<String>()


    @SuppressLint("ClickableViewAccessibility")
    fun attachMovableView(): View.OnTouchListener {
        return View.OnTouchListener(function = { view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                view.y = motionEvent.rawY - view.height / 2 - 100
                view.x = motionEvent.rawX - view.width / 2
            }

            true

        })
    }

    fun isUrlVideo(string: String): Boolean {
        return string.contains("videos")
    }

    fun getFileName(string: String): String {
        try {
            val newName =
                FilenameUtils.getName(string)
                    .replace("files/", "")
                    .replace("files%2F", "")
                    .replace("images%2F", "")
                    .replace("videos%2F", "")
            return newName.split("?")[0]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun pixelsToSp(sp: Int, context: Context): Float {
        return sp * context.resources.displayMetrics.scaledDensity
    }


    fun Any.prettyPrint() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        Log.d("TAG", "prettyPrint:DATA>>" + gson.toJson(this))
    }

    var likesScreenOpen = 0
    var topBitmap: Bitmap? = null

    var topHobby1 = "hobbies[1]"

    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }


    fun getFilePath(fileName: String, filePath: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val path =
                "$filePath/$fileName"
            path
        } else {
            val target = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            target.path
        }
    }


    fun scaleBitmap(input: Bitmap, maxBytes: Long): Bitmap? {

        val currentWidth = input.width
        val currentHeight = input.height
        val currentPixels = currentWidth * currentHeight
        // Get the amount of max pixels:
        // 1 pixel = 4 bytes (R, G, B, A)
        val maxPixels = maxBytes / 4 // Floored
        if (currentPixels <= maxPixels) {
            // Already correct size:
            return input
        }
        // Scaling factor when maintaining aspect ratio is the square root since x and y have a relation:
        val scaleFactor = sqrt(maxPixels / currentPixels.toDouble())
        val newWidthPx = floor(currentWidth * scaleFactor).toInt()
        val newHeightPx = floor(currentHeight * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(input, newWidthPx, newHeightPx, true)
    }

    fun getImageUri(inImage: Bitmap, activity: Activity): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveImageInQ(inImage, activity)
        else saveTheImageLegacy(inImage)
    }

    fun saveTheImageLegacy(bitmap: Bitmap): Uri? {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, "" + System.currentTimeMillis() + "file")
        val fos = FileOutputStream(image)
        fos.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }

        return Uri.fromFile(image)
    }

    @SuppressLint("InlinedApi")
    fun saveImageInQ(bitmap: Bitmap, activity: Activity): Uri? {
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


    fun listToString(list: List<String>?): String {
        var str = ""
        list?.let {
            for (i in it.indices) {
                str = if (str == "") {
                    it[i]
                } else {
                    str + "," + it[i]
                }
            }
        }
        return str
    }

    fun generateChatNode(currentUserId: Int, otherUserId: Int): String =
        if (currentUserId < otherUserId) "$currentUserId-$otherUserId"
        else "$otherUserId-$currentUserId"


    fun deletePhoto(file: String) {
        val file = File(file)
        if (file.exists() && file.canWrite()) {
            file.delete()
            Log.d("TAG", "deletePhoto: " + "deletephoto" + file)
        } else {
        }
    }


    fun getRandomInt(): String {
        val charset = ('1'..'9')

        return List(9) { charset.random() }
            .joinToString("")
    }


    fun Activity.checkIfLocationPermissionIsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }
}