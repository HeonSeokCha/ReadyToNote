package com.chs.readytonote

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.loader.content.CursorLoader
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


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
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        Log.d("returnCursor","${returnCursor}")
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

@GlideModule
class MyGlide : AppGlideModule()
