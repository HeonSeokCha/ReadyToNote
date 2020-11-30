package com.chs.readytonote.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Note
import com.chs.readytonote.entities.Todo

class NoteRepository (application: Application) {

    private val dao: NoteDao by lazy {
        val db = NotesDatabases.getInstance(application)
        db.noteDao()
    }

    fun getNotes(): LiveData<MutableList<Note>> = dao.getAllNotes()

    fun getTodos(): LiveData<MutableList<Todo>> = dao.getAllToDos()

    suspend fun insertNote(note:Note) {
        dao.insertNote(note)
    }

    suspend fun insertTodo(todo:Todo) {
        dao.insertTodo(todo)
    }

    suspend fun deleteNote(note:Note) {
        dao.deleteNote(note)
    }

    suspend fun deleteTodo(todo:Todo) {
        dao.deleteTodo(todo)
    }

    suspend fun allDelete() {
        dao.deleteNoteAll()
    }
}