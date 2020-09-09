package com.chs.readytonote

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader


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