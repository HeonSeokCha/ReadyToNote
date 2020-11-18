package com.chs.readytonote

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object Preferences {
    private const val DATA = "DATA"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    var data: String
        get() = preferences.getString(DATA, "Default") ?: ""
        set(value) = preferences.edit {
            putString(DATA, value)
        }
}

class ThemeInit : Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)
    }
}