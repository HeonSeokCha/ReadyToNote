package com.chs.readytonote.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chs.readytonote.entities.Note
import com.chs.readytonote.entities.Todo

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<MutableList<Note>>

    @Query("SELECT * FROM todos,notes where note_id = notes.id")
    fun getAllToDos(): LiveData<MutableList<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Delete()
    suspend fun deleteNote(note: Note)

    @Delete()
    suspend fun deleteTodo(todo: Todo)

    @Query("DELETE FROM notes")
    suspend fun deleteNoteAll() : Int
}