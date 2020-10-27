package com.chs.readytonote

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.loader.content.CursorLoader


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

internal fun calcRotate(file: String,sampleSize: Int) : Bitmap {
    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
    }

    val bitmap = BitmapFactory.decodeFile(file,options)
    val exif = ExifInterface(file)
    val matrix = Matrix()
    val orientation = exif.getAttributeInt (
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL,
    )
    when(orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
    }
    return Bitmap.createBitmap(bitmap, 0,0 ,
        bitmap.width, bitmap.height,
        matrix, true,
    )
}
