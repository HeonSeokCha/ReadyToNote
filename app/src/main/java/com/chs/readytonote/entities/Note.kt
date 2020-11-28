package com.chs.readytonote.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "date_time")
    val dateTime: String?,

    @ColumnInfo(name = "subtitle")
    val subtitle: String?,

    @ColumnInfo(name = "note_text")
    val noteText: String?,

    @ColumnInfo(name = "image_path")
    val imgPath: String?,

    @ColumnInfo(name = "color")
    val color: String?,

    @ColumnInfo(name = "web_link")
    val webLink: String?

): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    ) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(dateTime)
        parcel.writeString(subtitle)
        parcel.writeString(noteText)
        parcel.writeString(imgPath)
        parcel.writeString(color)
        parcel.writeString(webLink)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = CASCADE
        )
    ]
)
data class Todo(
    @ColumnInfo(name = "note_id")
    val noteId: Int?,

    @ColumnInfo(name = "todo_title")
    val todoTitle: String?,

    @ColumnInfo(name = "todo_check")
    val todoCheck: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}