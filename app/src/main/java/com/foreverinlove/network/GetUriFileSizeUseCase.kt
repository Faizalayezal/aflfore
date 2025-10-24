package com.foreverinlove.network

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.FileNotFoundException

class GetUriFileSizeUseCase {
    fun length(uri: Uri, contentResolver: ContentResolver)
            : Long {

        val assetFileDescriptor = try {
            contentResolver.openAssetFileDescriptor(uri, "r")
        } catch (e: FileNotFoundException) {
            null
        }
        // uses ParcelFileDescriptor#getStatSize underneath if failed
        val length = assetFileDescriptor?.use { it.length } ?: -1L
        if (length != -1L) {
            return length
        }

        // if "content://" uri scheme, try contentResolver table
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            return contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    // maybe shouldn't trust ContentResolver for size: https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex == -1) {
                        return@use -1L
                    }
                    cursor.moveToFirst()
                    return try {
                        cursor.getLong(sizeIndex)
                    } catch (_: Throwable) {
                        -1L
                    }
                } ?: -1L
        } else {
            return -1L
        }
    }

}