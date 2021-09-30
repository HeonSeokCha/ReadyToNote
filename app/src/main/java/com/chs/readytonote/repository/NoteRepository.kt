package com.chs.readytonote.repository

import android.app.Application
import com.chs.readytonote.dao.NoteDao
import com.chs.readytonote.database.NotesDatabases
import com.chs.readytonote.entities.Label
import com.chs.readytonote.entities.LabelCheck
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(application: Application) {

    private val dao: NoteDao by lazy {
        val db = NotesDatabases.getInstance(application)
        db.noteDao()
    }

    fun getNotes(): Flow<List<Note>> = dao.getAllNotes()

    fun searchNotes(searchWord: String): Flow<List<Note>> = dao.searchNotes(searchWord)

    fun getLabels(): Flow<List<Label>> = dao.getAllLabels()

    fun searchLabel(searchWord: String): Flow<List<Label>> = dao.searchLabel(searchWord)

    fun getCheckLabel(noteId: Int): Flow<LabelCheck> = dao.getCheckedLabel(noteId)

    suspend fun insertNote(note: Note): Long = dao.insertNote(note)

    suspend fun updateNote(note: Note) = dao.updateNote(note)

    suspend fun deleteNote(note: Note) = dao.deleteNote(note)

    suspend fun insertLabel(label: Label) = dao.insertLabel(label)

    suspend fun insertCheckLabel(labelCheck: LabelCheck) = dao.insertLabelCheck(labelCheck)

    suspend fun deleteCheckLabel(noteId: Int) = dao.deleteLabelCheck(noteId)

    suspend fun updateCheckLabel(labelCheck: LabelCheck) = dao.updateLabelCheck(labelCheck)

}

