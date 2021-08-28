package com.chs.readytonote

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore(name = Constants.DATA_STORE)

class DataStoreModule(private val context: Context) {

    private val uiKey = stringPreferencesKey(Constants.UI_STATUS)

    val getUIStatus: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[uiKey] ?: Constants.WHITE_MODE
        }

    suspend fun setUIStatus(uiValue: String) {
        context.dataStore.edit { preferences ->
            preferences[uiKey] = uiValue
        }
    }
}