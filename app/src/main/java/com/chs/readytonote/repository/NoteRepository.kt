package com.chs.readytonote.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.chs.readytonote.entities.Note

class NoteRepository (application: Application) {

    private val dao: NoteDao by lazy {
        val db = NotesDatabases.getInstance(application)
        db.noteDao()
    }

    fun getNotes(): LiveData<MutableList<Note>> = dao.getAllNotes()

    suspend fun insertNote(note: Note):Long = dao.insertNote(note)

    suspend fun updateNote(note: Note) {
        dao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }

    fun getLabels(): LiveData<MutableList<Label>> = dao.getAllLabels()

    suspend fun insertLabel(label: Label) {
        dao.insertLabel(label)
    }

    suspend fun allDelete() {
        dao.deleteNoteAll()
    }

    fun getCheckLabel(noteId: Int): LiveData<LabelCheck> =
        dao.getCheckedLabel(noteId)

    suspend fun insertCheckLabel(labelCheck: LabelCheck) {
        dao.insertLabelCheck(labelCheck)
    }

    suspend fun deleteCheckLabel(noteId: Int) {
        dao.deleteLabelCheck(noteId)
    }

    suspend fun updateCheckLabel(labelCheck: LabelCheck) {
        dao.updateLabelCheck(labelCheck)
    }
}

