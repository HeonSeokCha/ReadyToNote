package com.chs.readytonote.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "label")
data class Label(
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "checked")
    var checked: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}


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