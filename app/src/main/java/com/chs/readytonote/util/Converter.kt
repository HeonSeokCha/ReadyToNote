package com.chs.readytonote.util

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Converter {
    @TypeConverter
    fun listToJson(value: ArrayList<Int>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToList(value: String) = Json.decodeFromString<ArrayList<Int>>(value)

}