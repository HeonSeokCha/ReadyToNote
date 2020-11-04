package com.chs.readytonote

import android.app.Application
import androidx.lifecycle.LiveData
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Note

class NoteRepository (application: Application){

    private val dao: NoteDao by lazy {
        val db = NotesDatabases.getInstance(application)
        db.todoDao()
    }

    fun getNotes(): LiveData<MutableList<Note>> = dao.getAllNotes()

    suspend fun insert(note:Note) {
        dao.insertNote(note)
    }

    suspend fun delete(note:Note) {
        dao.deleteNote(note)
    }

    suspend fun allDelete() {
        dao.deleteAll()
    }
}