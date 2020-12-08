package com.chs.readytonote.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.Note
import com.chs.readytonote.entities.NoteWithLabelList

class NoteRepository (application: Application) {

    private val dao: NoteDao by lazy {
        val db = NotesDatabases.getInstance(application)
        db.noteDao()
    }

    fun getNotes(): LiveData<MutableList<Note>> = dao.getAllNotes()

    suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }

    fun getLabels(noteId: Int): LiveData<MutableList<Label>> = dao.getAllLabels(noteId)

    suspend fun insertLabel(label: Label) {
        dao.insertLabel(label)
    }

    suspend fun deleteLabel(label: Label) {
        dao.deleteLabel(label)
    }

    suspend fun allDelete() {
        dao.deleteNoteAll()
    }
}