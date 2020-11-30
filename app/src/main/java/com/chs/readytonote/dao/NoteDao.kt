package com.chs.readytonote.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.Note
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<MutableList<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete()
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes")
    suspend fun deleteNoteAll() : Int

    @Query("SELECT * FROM label")
    fun getAllLabels(): LiveData<MutableList<Label>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: Label)

    @Delete
    suspend fun deleteLabel(label: Label)
}