package com.chs.readytonote.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<MutableList<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

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

    @Query("SELECT * FROM label_check where note_id = :note_id")
    fun getCheckedLabel(note_id: Int): LiveData<LabelCheck>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabelCheck(check: LabelCheck)

    @Update
    suspend fun updateLabelCheck(check: LabelCheck)

    @Query("SELECT id FROM notes ORDER BY id DESC LIMIT 1")
    fun getLastNoteId(): LiveData<Int>
}