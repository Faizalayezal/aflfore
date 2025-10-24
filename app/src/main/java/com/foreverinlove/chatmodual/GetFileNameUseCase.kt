package com.foreverinlove.chatmodual

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

//file na name mate
class GetFileNameUseCase {

    @SuppressLint("Range")
    fun getName(uri: Uri, context: Context): String {
        var result: String? = null
        try {
            if (uri.scheme == "content") {
                try {
                    val cursor: Cursor =
                        context.contentResolver.query(uri, null, null, null, null)!!

                    if (cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                    cursor.close()
                }catch (e:Exception){e.printStackTrace()}
            }
            if (result == null) {
                result = uri.path
                val cut = result?.lastIndexOf('/')
                if (cut != -1) {
                    result = result?.substring(cut!! + 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result ?: ""
    }

}