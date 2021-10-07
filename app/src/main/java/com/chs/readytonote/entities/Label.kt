package com.chs.readytonote.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable


@Entity(tableName = "label")
@Parcelize
@Serializable
data class Label(
    @ColumnInfo(name = "title")
    val title: String?,
    var checked: Boolean = false
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}