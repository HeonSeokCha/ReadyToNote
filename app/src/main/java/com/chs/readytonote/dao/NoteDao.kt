package com.chs.readytonote.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chs.readytonote.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes():LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note:Note)

    @Delete()
    suspend fun deleteNote(note:Note)

    @Query("DELETE FROM notes")
    suspend fun deleteAll() : Int
}