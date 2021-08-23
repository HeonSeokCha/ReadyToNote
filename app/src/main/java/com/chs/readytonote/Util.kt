package com.chs.readytonote

import android.content.Context
import android.net.Uri

object Util {
    fun getRealPathFromURI(
        mContext: Context,
        contentUri: Uri
    ): String? {
        var path: String = ""
        var cursor = mContext.contentResolver.query(contentUri,
            null,null,null,null)
        if(cursor == null) {
            path = contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            path = cursor.getString(index)
            cursor.close()
        }
        return path
    }
}