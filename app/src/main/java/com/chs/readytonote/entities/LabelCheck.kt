package com.chs.readytonote.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label_check")
data class LabelCheck(
    @ColumnInfo(name = "note_id")
    var note_id: Int,
    @ColumnInfo(name = "checked_label_id")
    var checkedLabelId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}