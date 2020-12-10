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


@Entity(tableName = "label_check",
        foreignKeys = [
            ForeignKey(
                entity = Note::class,
                parentColumns = ["id"],
                childColumns = ["note_id"],
                onDelete = CASCADE
        )]
)
data class LabelCheck(
    @ColumnInfo(name = "note_id")
    val note_id: Int,
    @ColumnInfo(name = "checked_label_id")
    val checkedLabelId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}