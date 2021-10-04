package com.chs.readytonote.dao

import androidx.room.*
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM notes where UPPER(title) LIKE '%'||UPPER(:searchWord)||'%' or UPPER(subtitle) LIKE '%'||UPPER(:searchWord)||'%'")
    fun searchNotes(searchWord: String): Flow<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes")
    suspend fun deleteNoteAll(): Int

    @Query("SELECT * FROM label")
    fun getAllLabels(): Flow<List<Label>>

    @Query("SELECT * FROM label where UPPER(title) LIKE '%'||UPPER(:searchWord)||'%'")
    fun searchLabel(searchWord: String): Flow<List<Label>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: Label)

}