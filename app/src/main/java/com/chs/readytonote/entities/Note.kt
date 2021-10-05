package com.chs.readytonote.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "label")
    val labelTitle: String?,

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

) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}