package com.chs.readytonote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.Note
import com.chs.readytonote.util.Converter

@Database(
    entities = [
        Note::class,
        Label::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class NotesDatabases : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabases? = null

        fun getInstance(context: Context): NotesDatabases {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotesDatabases::class.java, "notes_db"
                    ).build()
                }
                return instance
            }
        }
    }
}