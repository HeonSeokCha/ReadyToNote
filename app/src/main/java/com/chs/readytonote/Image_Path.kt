package com.chs.readytonote

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule


@GlideModule
class MyGlide : AppGlideModule()

internal fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val loader = CursorLoader(context, contentUri, proj, null, null, null)
    val cursor: Cursor? = loader.loadInBackground()
    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val result = cursor.getString(column_index)
    cursor.close()
    return result
}

internal fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val projection = arrayOf(MediaStore.MediaColumns._ID)
    val returnCursor = this.query(fileUri, projection, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(MediaStore.MediaColumns._ID)
        returnCursor.moveToFirst()
        val id = returnCursor.getInt(returnCursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
        returnCursor.close()
    }
    return name
}
