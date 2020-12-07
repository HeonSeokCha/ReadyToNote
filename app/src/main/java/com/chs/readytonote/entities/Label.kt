package com.chs.readytonote.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*

@Entity(tableName = "label")
data class Label(
    @ColumnInfo(name = "note_id")
    val note_id: Int?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "checked")
    var checked: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    constructor() : this(null,"필기",false)
}

data class NoteWithLabelList(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "note_id"
    )
    val labelList: List<Label>
)