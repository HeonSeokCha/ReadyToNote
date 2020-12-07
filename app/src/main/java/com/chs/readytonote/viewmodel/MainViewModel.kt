package com.chs.readytonote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chs.readytonote.entities.Label
import com.chs.readytonote.repository.NoteRepository
import com.chs.readytonote.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application):AndroidViewModel(application) {

    private var repository: NoteRepository = NoteRepository(application)

    fun getAllNotes() = repository.getNotes()

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(note)
    }

    fun deleteNote(note:Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    fun allDelete() = viewModelScope.launch(Dispatchers.IO) {
        repository.allDelete()
    }

    fun getAllLabel() = repository.getLabels()

    fun getAllTest() = repository.getTest()

    fun insertLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertLabel(label)
    }

    fun deleteLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteLabel(label)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.allDelete()
    }
}