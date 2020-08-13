package com.chs.readytonote

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import androidx.loader.content.CursorLoader

fun getPath(context: Context, uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri)
        val split =
            docId.split(":".toRegex()).toTypedArray()
        var contentUri: Uri? = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        return getDataColumn(context, contentUri, selection, selectionArgs)
}

fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor =
            context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val loader = CursorLoader(context, contentUri, proj, null, null, null)
    val cursor: Cursor? = loader.loadInBackground()
    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val result = cursor.getString(column_index)
    cursor.close()
    return result
}