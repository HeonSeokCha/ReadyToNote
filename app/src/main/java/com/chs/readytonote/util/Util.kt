package com.chs.readytonote.util

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.preferencesDataStore

object Util {
    val Context.dataStore by preferencesDataStore(name = Constants.DATA_STORE)

    fun getRealPathFromURI(
        mContext: Context,
        contentUri: Uri
    ): String? {
        var path: String = ""
        var cursor = mContext.contentResolver.query(
            contentUri,
            null, null, null, null
        )
        if (cursor == null) {
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