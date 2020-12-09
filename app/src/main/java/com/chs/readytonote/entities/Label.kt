package com.chs.readytonote.entities

import androidx.room.*

@Entity(tableName = "label",
    foreignKeys = [ForeignKey(entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("note_id")
    )]
)
data class Label(
    @ColumnInfo(name = "note_id")
    var note_id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "checked")
    var checked: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

data class NoteWithLabelList(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "note_id"
    )
    val labelList: List<Label>
)