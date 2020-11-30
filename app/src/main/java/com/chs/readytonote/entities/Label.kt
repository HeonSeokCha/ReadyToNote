package com.chs.readytonote.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "label")
data class Label(
    @ColumnInfo(name = "name")
    val name: String
)
