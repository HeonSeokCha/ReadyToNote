package com.chs.readytonote.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "note_id")
    val noteId: Int,

    @ColumnInfo(name = "todo_text")
    val title: String,

    @ColumnInfo(name = "todo_check")
    val todoCheck: Boolean,
)

